package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.dto.post.PostDto;

import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.mapper.ImageMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для загрузки, хранения и получения изображений постов.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;
    private final PostService postService;

    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    /**
     * Возвращает изображения поста.
     *
     * @param postId идентификатор поста
     * @return список изображений поста
     */
    public List<ImageDto> getPostImages(long postId) {
        postService.findById(postId);
        log.info("Получение изображений для поста id={}", postId);

        return imageRepository.findByPostId(postId)
                .stream()
                .map(ImageMapper::mapToImageDto)
                .toList();
    }

    /**
     * Сохраняет переданные изображения и связывает их с постом.
     *
     * @param postId идентификатор поста
     * @param files файлы изображений
     * @return сохранённые изображения
     */
    public List<ImageDto> saveImages(long postId, List<MultipartFile> files) {
        log.info("Сохранение {} изображений для поста id={}", files.size(), postId);
        return files.stream()
                .map(file -> saveImage(postId, file))
                .toList();
    }

    /**
     * Проверяет пост, валидирует и сохраняет один файл изображения.
     *
     * @param postId идентификатор поста
     * @param file файл изображения
     * @return сохранённое изображение
     */
    private ImageDto saveImage(long postId, MultipartFile file) {
        PostDto post = postService.findById(postId);

        validateImageFile(file);

        Path filePath = generateFilePath(post, file);

        saveFileToDisk(file, filePath);

        Image image = createAndSaveImage(postId, file, filePath);

        log.info("Сохранено изображение id={} для поста id={}", image.getId(), postId);
        return ImageMapper.mapToImageDto(image);
    }

    /**
     * Проверяет, что файл не пуст и имеет допустимое расширение и MIME-тип.
     *
     * @param file файл изображения
     * @throws ParameterNotValidException если файл пуст либо расширение/тип недопустимы
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ParameterNotValidException("file", "Файл не может быть пустым");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ParameterNotValidException("file", "Файл должен иметь расширение");
        }

        String extension = StringUtils.getFilenameExtension(originalFilename).toLowerCase();
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif");

        if (!allowedExtensions.contains(extension)) {
            throw new ParameterNotValidException("file",
                    "Недопустимое расширение: " + extension + ". Разрешены: " + allowedExtensions);
        }

        String contentType = file.getContentType();
        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/gif");

        if (contentType == null || !allowedMimeTypes.contains(contentType)) {
            throw new ParameterNotValidException("file",
                    "Недопустимый тип файла: " + contentType + ". Разрешены: " + allowedMimeTypes);
        }
    }

    /**
     * Формирует уникальный путь для сохранения файла изображения на диске.
     *
     * @param post пост, к которому относится изображение
     * @param file исходный файл изображения
     * @return путь для сохранения файла
     */
    private Path generateFilePath(PostDto post, MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String uniqueFileName = String.format("%s.%s", UUID.randomUUID(), extension);

        Path uploadPath = Paths.get(imageDirectory,
                String.valueOf(post.getAuthorId()),
                String.valueOf(post.getId()));

        return uploadPath.resolve(uniqueFileName);
    }

    /**
     * Записывает содержимое файла на диск, создавая директории при необходимости.
     *
     * @param file файл изображения
     * @param filePath путь для сохранения
     * @throws ImageFileException если запись файла завершилась ошибкой
     */
    private void saveFileToDisk(MultipartFile file, Path filePath) {
        try {
            Path parentDir = filePath.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new ImageFileException("Ошибка сохранения файла: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Сохраняет метаданные изображения в БД; при ошибке удаляет уже записанный файл.
     *
     * @param postId идентификатор поста
     * @param file исходный файл изображения
     * @param filePath путь, по которому файл сохранён на диске
     * @return сохранённое изображение
     */
    private Image createAndSaveImage(long postId, MultipartFile file, Path filePath) {
        Image image = new Image();
        image.setPostId(postId);
        image.setOriginalFileName(file.getOriginalFilename());
        image.setFilePath(filePath.toString());

        try {
            return imageRepository.save(image);
        } catch (Exception e) {
            deleteFileSafely(filePath);
            throw e;
        }
    }

    /**
     * Удаляет файл с диска, если он существует, подавляя и логируя ошибку удаления.
     *
     * @param filePath путь к файлу
     */
    private void deleteFileSafely(Path filePath) {
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Файл удалён после ошибки БД: {}", filePath);
            }
        } catch (IOException deleteError) {
            log.error("Не удалось удалить файл после ошибки БД: {}", filePath, deleteError);
        }
    }


    /**
     * Загружает содержимое изображения с диска.
     *
     * @param imageId идентификатор изображения
     * @return данные файла и исходное имя изображения
     */
    public ImageData getImageData(long imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() ->
                new NotFoundException("Изображение с id = " + imageId + " не найдено"));
        byte[] data = loadFile(image);

        return new ImageData(data, image.getOriginalFileName());
    }

    /**
     * Читает содержимое файла изображения с диска.
     *
     * @param image изображение, для которого нужно прочитать файл
     * @return байты содержимого файла
     * @throws ImageFileException если файл отсутствует или не может быть прочитан
     */
    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла.  Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName(), e);
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
    }

}
