package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Модель изображения, хранимая в таблице {@code image_storage}.
 */
@Data
@EqualsAndHashCode(of = {"id"})

public class Image {
    Long id;
    long postId;
    String originalFileName;
    String filePath;
}
