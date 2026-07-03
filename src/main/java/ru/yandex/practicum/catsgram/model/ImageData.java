package ru.yandex.practicum.catsgram.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Содержимое файла изображения и его исходное имя, отдаваемые при скачивании.
 */
@Data
@AllArgsConstructor
public class ImageData {
    private final byte[] data;
    private final String name;
}