package ru.yandex.practicum.catsgram.dal;

import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для операций с постами в БД.
 */
@Log4j2
@Repository
public class PostRepository extends BaseRepository<Post> {
    private static final String FIND_ALL_DESC_QUERY =
            "SELECT * FROM posts ORDER BY post_date DESC, id DESC LIMIT ? OFFSET ?";

    private static final String FIND_ALL_ASC_QUERY =
            "SELECT * FROM posts ORDER BY post_date ASC, id ASC LIMIT ? OFFSET ?";
    private static final String FIND_BY_AUTHOR_ID = "SELECT * FROM posts WHERE author_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM posts WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO posts (author_id, description, post_date)" +
            "VALUES (?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE posts SET description = ? WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Возвращает страницу постов с заданной сортировкой.
     *
     * @param offset смещение от начала списка
     * @param limit размер страницы
     * @param sort направление сортировки: {@code asc} или {@code desc} (по умолчанию desc)
     * @return список постов
     */
    public List<Post> findAll(int offset, int limit, String sort) {
        String query = sort.equalsIgnoreCase("asc") ? FIND_ALL_ASC_QUERY : FIND_ALL_DESC_QUERY;
        log.debug("Запрос постов из БД: offset={}, limit={}, sort={}", offset, limit, sort);

        return findMany(query, limit, offset);
    }

    /**
     * Возвращает все посты указанного автора.
     *
     * @param authorId идентификатор автора
     * @return список постов автора
     */
    public List<Post> findByAuthorId(Long authorId) {
        return findMany(FIND_BY_AUTHOR_ID, authorId);
    }

    /**
     * Ищет пост по идентификатору.
     *
     * @param postId идентификатор поста
     * @return найденный пост либо {@link Optional#empty()}
     */
    public Optional<Post> findById(long postId) {
        return findOne(FIND_BY_ID_QUERY, postId);
    }

    /**
     * Сохраняет новый пост и присваивает ему сгенерированный идентификатор.
     *
     * @param post данные поста для сохранения
     * @return сохранённый пост с заполненным id
     */
    public Post save(Post post) {
        long id = insert(
                INSERT_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    /**
     * Обновляет данные существующего поста.
     *
     * @param post пост с новыми данными
     * @return обновлённый пост
     */
    public Post update(Post post) {
        update(
                UPDATE_QUERY,
                post.getDescription(),
                post.getId()
        );
        return post;
    }
}
