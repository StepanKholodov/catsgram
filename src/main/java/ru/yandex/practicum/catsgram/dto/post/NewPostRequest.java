package ru.yandex.practicum.catsgram.dto.post;

import lombok.Data;

/**
 * Данные, передаваемые клиентом при создании нового поста.
 */
@Data
public class NewPostRequest {
    private Long authorId;
    private String description;

}
