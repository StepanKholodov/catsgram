package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

/**
 * REST-контроллер для операций с постами.
 */
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Возвращает страницу постов с заданной сортировкой.
     *
     * @param from смещение от начала списка
     * @param size размер страницы, должен быть больше нуля
     * @param sort направление сортировки: asc или desc
     * @return страница постов
     */
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

    /**
     * Создаёт новый пост.
     *
     * @param post данные нового поста
     * @return созданный пост
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDto create(@RequestBody NewPostRequest post) {
        return postService.create(post);
    }

    /**
     * Обновляет существующий пост.
     *
     * @param postId идентификатор поста
     * @param post новые данные поста
     * @return обновлённый пост
     */
    @PutMapping("/{postId}")
    public PostDto update(@PathVariable("postId") long postId, @RequestBody UpdatePostRequest post) {
        return postService.update(postId, post);
    }

    /**
     * Возвращает пост по идентификатору.
     *
     * @param id идентификатор поста
     * @return найденный пост
     */
    @GetMapping("/{id}")
    public PostDto getById(@PathVariable Long id) {
        return postService.findById(id);
    }

}
