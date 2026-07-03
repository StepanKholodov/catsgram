package ru.yandex.practicum.catsgram.dto.post;

import lombok.Data;

/**
 * Данные, передаваемые клиентом при обновлении поста.
 */
@Data
public class UpdatePostRequest {
    private String description;

    /**
     * Проверяет, указано ли новое описание поста.
     *
     * @return {@code true}, если description не null и не пусто
     */
    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }
}
