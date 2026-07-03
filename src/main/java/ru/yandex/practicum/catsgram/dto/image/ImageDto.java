package ru.yandex.practicum.catsgram.dto.image;

import lombok.Data;

/**
 * Представление изображения, отдаваемое клиенту.
 */
@Data
public class ImageDto {
    private long id;
    private long postId;
    private String originalFileName;
}
