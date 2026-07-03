package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.model.Image;

/**
 * Преобразует модель {@link Image} в её DTO-представление.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageMapper {

    /**
     * Преобразует модель изображения в DTO для ответа клиенту.
     *
     * @param image модель изображения
     * @return DTO изображения
     */
    public static ImageDto mapToImageDto(Image image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setOriginalFileName(image.getOriginalFileName());
        dto.setPostId(image.getPostId());
        return dto;
    }
}
