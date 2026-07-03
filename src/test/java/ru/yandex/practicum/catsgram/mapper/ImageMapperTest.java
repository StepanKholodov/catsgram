package ru.yandex.practicum.catsgram.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.model.Image;

import static org.assertj.core.api.Assertions.assertThat;

class ImageMapperTest {

    @Test
    void mapToImageDto_mapsAllFields() {
        Image image = new Image();
        image.setId(5L);
        image.setPostId(3L);
        image.setOriginalFileName("cat.jpg");
        image.setFilePath("/some/path/cat.jpg");

        ImageDto dto = ImageMapper.mapToImageDto(image);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getPostId()).isEqualTo(3L);
        assertThat(dto.getOriginalFileName()).isEqualTo("cat.jpg");
    }

    @Test
    void mapToImageDto_withNullFileName_mapsNullFileName() {
        Image image = new Image();
        image.setId(1L);
        image.setPostId(2L);
        image.setOriginalFileName(null);

        ImageDto dto = ImageMapper.mapToImageDto(image);

        assertThat(dto.getOriginalFileName()).isNull();
    }
}
