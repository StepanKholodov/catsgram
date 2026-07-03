package ru.yandex.practicum.catsgram.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PostMapperTest {

    @Test
    void mapToPost_setsAllFields() {
        NewPostRequest request = new NewPostRequest();
        request.setAuthorId(42L);
        request.setDescription("My cat");

        Post post = PostMapper.mapToPost(request);

        assertThat(post.getAuthorId()).isEqualTo(42L);
        assertThat(post.getDescription()).isEqualTo("My cat");
        assertThat(post.getPostDate()).isNotNull();
    }

    @Test
    void mapToPostDto_mapsAllFields() {
        Post post = new Post();
        post.setId(10L);
        post.setAuthorId(2L);
        post.setDescription("Fluffy");
        post.setPostDate(Instant.now());

        PostDto dto = PostMapper.mapToPostDto(post);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthorId()).isEqualTo(2L);
        assertThat(dto.getDescription()).isEqualTo("Fluffy");
        assertThat(dto.getPostDate()).isNotNull();
    }

    @Test
    void updatePostFields_updatesDescriptionWhenPresent() {
        Post post = new Post();
        post.setDescription("old");

        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("new description");

        Post updated = PostMapper.updatePostFields(post, request);

        assertThat(updated.getDescription()).isEqualTo("new description");
    }

    @Test
    void updatePostFields_doesNotUpdateDescriptionWhenNull() {
        Post post = new Post();
        post.setDescription("keep");

        UpdatePostRequest request = new UpdatePostRequest();

        Post updated = PostMapper.updatePostFields(post, request);

        assertThat(updated.getDescription()).isEqualTo("keep");
    }

    @Test
    void updatePostFields_doesNotUpdateDescriptionWhenBlank() {
        Post post = new Post();
        post.setDescription("keep");

        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("  ");

        Post updated = PostMapper.updatePostFields(post, request);

        assertThat(updated.getDescription()).isEqualTo("keep");
    }
}
