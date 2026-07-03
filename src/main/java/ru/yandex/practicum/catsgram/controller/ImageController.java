package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

/**
 * REST-контроллер для загрузки и получения изображений постов.
 */
@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    /**
     * Возвращает изображения поста.
     *
     * @param postId идентификатор поста
     * @return список изображений поста
     */
    @GetMapping("/posts/{postId}/images")
    public List<ImageDto> getPostImages(@PathVariable("postId") long postId) {
        return imageService.getPostImages(postId);
    }

    /**
     * Загружает и сохраняет изображения для поста.
     *
     * @param postId идентификатор поста
     * @param files файлы изображений
     * @return сохранённые изображения
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/images")
    public List<ImageDto> addPostImages(@PathVariable("postId") long postId,
                                     @RequestParam("image") List<MultipartFile> files) {
        return imageService.saveImages(postId, files);
    }

    /**
     * Скачивает содержимое изображения по идентификатору.
     *
     * @param imageId идентификатор изображения
     * @return бинарные данные изображения с заголовком для скачивания
     */
    @GetMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadImage(@PathVariable long imageId) {
        ImageData imageData = imageService.getImageData(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(imageData.getName())
                        .build()
        );

        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }
}