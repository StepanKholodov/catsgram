package ru.yandex.practicum.catsgram.dal.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private PostRowMapper mapper;

    @Test
    void mapRow_mapsAllFields() throws SQLException {
        Instant now = Instant.now();
        Timestamp ts = Timestamp.from(now);

        when(resultSet.getLong("id")).thenReturn(10L);
        when(resultSet.getLong("author_id")).thenReturn(2L);
        when(resultSet.getString("description")).thenReturn("Cute cat");
        when(resultSet.getTimestamp("post_date")).thenReturn(ts);

        Post post = mapper.mapRow(resultSet, 0);

        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(10L);
        assertThat(post.getAuthorId()).isEqualTo(2L);
        assertThat(post.getDescription()).isEqualTo("Cute cat");
        assertThat(post.getPostDate()).isEqualTo(now);
    }
}
