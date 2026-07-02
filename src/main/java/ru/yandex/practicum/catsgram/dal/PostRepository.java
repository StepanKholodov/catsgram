package ru.yandex.practicum.catsgram.dal;

import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.exception.InternalServerException;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Log4j2
@Repository
public class PostRepository extends BaseRepository<Post> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM posts";
    private static final String FIND_BY_AUTHOR_ID = "SELECT * FROM posts WHERE author_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM posts WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO posts (author_id, description, post_date)" +
            "VALUES (?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE posts SET description = ? WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Post> findByAuthorId(Long author_id) {
        return findOne(FIND_BY_AUTHOR_ID, author_id);
    }

    public Optional<Post> findById(long postId) {
        log.info("вошли в поиск по id");
        return findOne(FIND_BY_ID_QUERY, postId);
    }

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

    public Post update(Post post) {
        log.info("начали обновлять пост");
        update(
                UPDATE_QUERY,
                post.getDescription(),
                post.getId()
        );
        log.info("закончили обновлять пост");
        return post;
    }
}

