package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.util.Collection;


/**
 * Сервис для операций с постами: создание, поиск и обновление.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * Возвращает страницу постов с заданной сортировкой.
     *
     * @param from смещение от начала списка
     * @param size размер страницы
     * @param sort направление сортировки: asc или desc
     * @return страница постов
     */
    public Collection<PostDto> findAll(int from, int size, String sort) {
        log.info("Получение постов: from={}, size={}, sort={}", from, size, sort);
        return postRepository.findAll(from, size, sort).stream()
                .map(PostMapper::mapToPostDto)
                .toList();
    }

    /**
     * Создаёт пост после проверки автора и описания.
     *
     * @param request данные нового поста
     * @return созданный пост
     */
    public PostDto create(NewPostRequest request) {
        if (request.getAuthorId() == null) {
            throw new ConditionsNotMetException("Автор должен быть указан");
        }
        userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Автор не найден"));

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Post post = PostMapper.mapToPost(request);
        post = postRepository.save(post);
        log.info("Создан пост id={} для автора id={}", post.getId(), post.getAuthorId());

        return PostMapper.mapToPostDto(post);
    }

    /**
     * Обновляет существующий пост.
     *
     * @param postId идентификатор поста
     * @param request новые данные поста
     * @return обновлённый пост
     */
    public PostDto update(Long postId, UpdatePostRequest request) {
        Post updatedPost = postRepository.findById(postId)
                .map(post -> PostMapper.updatePostFields(post, request))
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
        updatedPost = postRepository.update(updatedPost);
        log.info("Обновлён пост id={}", postId);
        return PostMapper.mapToPostDto(updatedPost);

    }

    /**
     * Возвращает пост по идентификатору.
     *
     * @param id идентификатор поста
     * @return найденный пост
     */
    public PostDto findById(Long id) {
        return postRepository.findById(id)
                .map(PostMapper::mapToPostDto)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
    }

}
