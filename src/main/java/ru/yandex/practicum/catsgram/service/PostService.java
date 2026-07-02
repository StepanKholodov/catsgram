package ru.yandex.practicum.catsgram.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Log4j2
@Service
public class PostService {
    private final UserService userService;
    private final  PostRepository postRepository;

    public PostService(UserService userService, PostRepository postRepository) {
        this.userService = userService;
        this.postRepository = postRepository;
    }

    public Collection<PostDto> findAll(int from, int size, String sort) {
        Comparator<Post> comparator = Comparator.comparing(Post::getPostDate);

        if (sort.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        } else if (!sort.equalsIgnoreCase("asc")) {
            throw new IllegalArgumentException("Некорректный параметр sort: " + sort);
        }

        return postRepository.findAll().stream()
                .sorted(comparator)
                .skip(from)
                .limit(size)
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }

    public PostDto create(NewPostRequest request) {
        if (request.getAuthorId() == null) {
            throw new ConditionsNotMetException("Автор должен быть указан");
        }
        if (userService.getUserById(request.getAuthorId()) == null) {
            throw new NotFoundException("Автор с id = " + request.getAuthorId() + " не найден");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Post post = PostMapper.mapToPost(request);
        post = postRepository.save(post);

        return PostMapper.mapToPostDto(post);
    }

    public PostDto update(Long postId, UpdatePostRequest request) {
        log.info("вошли в метод сервера");
        Post updatedPost = postRepository.findById(postId)
                .map(post -> PostMapper.updatePostFields(post,request))
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
        log.info("закончили обработку");
        updatedPost = postRepository.update(updatedPost);
        return PostMapper.mapToPostDto(updatedPost);

    }

    public PostDto findById(Long id) {
        return postRepository.findById(id)
                .map(PostMapper::mapToPostDto)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
    }

}