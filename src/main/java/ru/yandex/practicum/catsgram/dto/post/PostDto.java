package ru.yandex.practicum.catsgram.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

/**
 * Представление поста, отдаваемое клиенту.
 */
@Data
public class PostDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private Long authorId;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant postDate = Instant.now();
}
