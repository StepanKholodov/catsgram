package ru.yandex.practicum.catsgram.mapper;

import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.dto.user.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UserDto;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;

public final class PostMapper {

    public static Post mapToPost(NewPostRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setDescription(request.getDescription());
        post.setPostDate(Instant.now());

        return post;
    }

    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setDescription(post.getDescription());
        dto.setAuthorId(post.getAuthorId());
        dto.setPostDate(Instant.now());
        return dto;
    }

    public static Post updatePostFields(Post post, UpdatePostRequest request) {
        if (request.hasDescription()) {
            post.setDescription(request.getDescription());
        }
        return post;
    }
}
