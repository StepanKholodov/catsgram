package ru.yandex.practicum.catsgram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void create_whenAuthorIdIsNull_throwsConditionsNotMetException() {
        NewPostRequest request = new NewPostRequest();
        request.setDescription("Some description");
        assertThatThrownBy(() -> postService.create(request))
                .isInstanceOf(ConditionsNotMetException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void create_whenAuthorNotFound_throwsNotFoundException() {
        NewPostRequest request = new NewPostRequest();
        request.setAuthorId(99L);
        request.setDescription("description");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.create(request))
                .isInstanceOf(NotFoundException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void create_whenDescriptionIsNull_throwsConditionsNotMetException() {
        NewPostRequest request = new NewPostRequest();
        request.setAuthorId(1L);
        User author = new User();
        author.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        assertThatThrownBy(() -> postService.create(request))
                .isInstanceOf(ConditionsNotMetException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void create_whenDescriptionIsBlank_throwsConditionsNotMetException() {
        NewPostRequest request = new NewPostRequest();
        request.setAuthorId(1L);
        request.setDescription("   ");
        User author = new User();
        author.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        assertThatThrownBy(() -> postService.create(request))
                .isInstanceOf(ConditionsNotMetException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void create_whenValidRequest_savesAndReturnsDto() {
        NewPostRequest request = new NewPostRequest();
        request.setAuthorId(1L);
        request.setDescription("Lovely cat");
        User author = new User();
        author.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        Post savedPost = new Post();
        savedPost.setId(10L);
        savedPost.setAuthorId(1L);
        savedPost.setDescription("Lovely cat");
        savedPost.setPostDate(Instant.now());
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        PostDto result = postService.create(request);
        assertThat(result.getDescription()).isEqualTo("Lovely cat");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void findById_whenPostNotFound_throwsNotFoundException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenPostExists_returnsDto() {
        Post post = new Post();
        post.setId(5L);
        post.setAuthorId(2L);
        post.setDescription("Cat nap");
        post.setPostDate(Instant.now());
        when(postRepository.findById(5L)).thenReturn(Optional.of(post));
        PostDto result = postService.findById(5L);
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getDescription()).isEqualTo("Cat nap");
    }

    @Test
    void update_whenPostNotFound_throwsNotFoundException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.update(99L, new UpdatePostRequest()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenPostExists_updatesAndReturnsDto() {
        Post existing = new Post();
        existing.setId(1L);
        existing.setAuthorId(2L);
        existing.setDescription("old");
        existing.setPostDate(Instant.now());
        when(postRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(postRepository.update(any(Post.class))).thenReturn(existing);
        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("new description");
        PostDto result = postService.update(1L, request);
        assertThat(result).isNotNull();
        verify(postRepository).update(any(Post.class));
    }

    @Test
    void findAll_delegatesToRepository() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setDescription("test");
        post.setPostDate(Instant.now());
        when(postRepository.findAll(0, 10, "desc")).thenReturn(List.of(post));
        Collection<PostDto> result = postService.findAll(0, 10, "desc");
        assertThat(result).hasSize(1);
    }

    @Test
    void findAll_withAscSort_delegatesToRepository() {
        when(postRepository.findAll(0, 5, "asc")).thenReturn(List.of());
        Collection<PostDto> result = postService.findAll(0, 5, "asc");
        assertThat(result).isEmpty();
        verify(postRepository).findAll(0, 5, "asc");
    }
}
