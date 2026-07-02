package ru.yandex.practicum.catsgram.dto.post;

import lombok.Data;

@Data
public class NewPostRequest {
    private Long authorId;
    private String description;

}
