package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.dto.post.PostDto;

import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.ImageMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private  final ImageRepository imageRepository;
    private  final  PostService postService;

    // директория для хранения изображений
    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    public List<ImageDto> getPostImages(long postId) {
        return imageRepository.findAll()
                .stream()
                .filter(image -> image.getPostId() == postId)
                .map(ImageMapper::mapToImageDto)
                .collect(Collectors.toList());
    }

    public List<ImageDto> saveImages(long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> saveImage(postId, file))
                .collect(Collectors.toList());
    }

    // сохранение отдельного изображения, связанного с указанным постом
    private ImageDto saveImage(long postId, MultipartFile file) {
        PostDto post = postService.findById(postId);

        Path filePath = saveFile(file, post);

        // создание объекта изображения и заполнение его данными
        Image image = new Image();
        image.setFilePath(filePath.toString());
        image.setPostId(postId);
        // запоминаем название файла, которое было при его передаче
        image.setOriginalFileName(file.getOriginalFilename());


        imageRepository.save(image);

        return ImageMapper.mapToImageDto(image);
    }

    // сохранение файла изображения
    private Path saveFile(MultipartFile file, PostDto post) {
        try {
            // формирование уникального названия файла на основе текущего времени и расширения оригинального файла
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));

            // формирование пути для сохранения файла с учётом идентификаторов автора и поста
            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()),
                    String.valueOf(post.getId()));
            Path filePath = uploadPath.resolve(uniqueFileName);

            // создаём директории, если они ещё не созданы
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // сохраняем файл по сформированному пути
            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // загружаем данные указанного изображения с диска
    public ImageData getImageData(long imageId) {

        Image image = imageRepository.findById(imageId).orElseThrow(() ->
                new NotFoundException("Изображение с id = " + imageId + " не найдено"));
        // загрузка файла с диска
        byte[] data = loadFile(image);

        return new ImageData(data, image.getOriginalFileName());
    }

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