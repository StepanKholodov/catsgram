package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;


/**
 * Модель поста, хранимая в таблице {@code posts}.
 */
@Data
@EqualsAndHashCode(of = {"id"})

public class Post {
    Long id;
    Long authorId;
    String description;
    Instant postDate;
}
