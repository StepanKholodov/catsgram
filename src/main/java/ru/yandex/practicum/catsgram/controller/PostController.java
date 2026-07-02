package ru.yandex.practicum.catsgram.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@Log4j2
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public Collection<PostDto> findAll(@RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(defaultValue = "desc") String sort) {
        if (size <= 0) {
            throw new ParameterNotValidException("size",
                    "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from",
                    "Начало выборки не может быть меньше нуля");
        }
        if (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc")) {
            throw new ParameterNotValidException("sort",
                    "Получено: " + sort + ", должно быть: asc или desc");
        }

        return postService.findAll(from, size, sort);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDto create(@RequestBody NewPostRequest post) {
        return postService.create(post);
    }

    @PutMapping("{postId}")
    public PostDto update(@PathVariable("postId") long postId, @RequestBody UpdatePostRequest post) {
        log.info("запустили метод обновления поста с id={}",postId);
        return postService.update(postId, post);
    }

    @GetMapping("{id}")
    public PostDto getById(@PathVariable Long id) {
        return postService.findById(id);
    }

}