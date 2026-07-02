package ru.yandex.practicum.catsgram.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageDto {
    private long id;
    private long postId;
    private String originalFileName;
}
