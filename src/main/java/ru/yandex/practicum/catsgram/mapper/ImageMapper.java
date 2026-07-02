package ru.yandex.practicum.catsgram.mapper;

import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.model.Image;

public class ImageMapper {

    public static ImageDto mapToImageDto(Image image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setOriginalFileName(image.getOriginalFileName());
        dto.setPostId(image.getPostId());
        return dto;
    }
}
