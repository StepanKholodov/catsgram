package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;

/**
 * Преобразует между моделью {@link Post} и её DTO-представлениями.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostMapper {

    /**
     * Создаёт новый пост из данных запроса на создание.
     *
     * @param request данные нового поста
     * @return новая модель поста с текущей датой публикации
     */
    public static Post mapToPost(NewPostRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setDescription(request.getDescription());
        post.setPostDate(Instant.now());

        return post;
    }

    /**
     * Преобразует модель поста в DTO для ответа клиенту.
     *
     * @param post модель поста
     * @return DTO поста
     */
    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setDescription(post.getDescription());
        dto.setAuthorId(post.getAuthorId());
        dto.setPostDate(post.getPostDate());
        return dto;
    }

    /**
     * Применяет к существующему посту переданные в запросе поля.
     *
     * @param post обновляемый пост
     * @param request данные для обновления
     * @return тот же пост с обновлёнными полями
     */
    public static Post updatePostFields(Post post, UpdatePostRequest request) {
        if (request.hasDescription()) {
            post.setDescription(request.getDescription());
        }
        return post;
    }
}
