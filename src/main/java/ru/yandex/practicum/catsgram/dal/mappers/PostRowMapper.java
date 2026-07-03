package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Преобразует строку {@link ResultSet} таблицы {@code posts} в модель {@link Post}.
 */
@Component
public class PostRowMapper implements RowMapper<Post> {

    /**
     * Читает текущую строку результата и создаёт из неё пост.
     *
     * @param rs результат SQL-запроса
     * @param rowNum номер строки
     * @return созданный пост
     * @throws SQLException при ошибке чтения данных из {@link ResultSet}
     */
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setAuthorId(rs.getLong("author_id"));
        post.setDescription(rs.getString("description"));

        Timestamp postDate = rs.getTimestamp("post_date");
        post.setPostDate(postDate.toInstant());

        return post;
    }
}
