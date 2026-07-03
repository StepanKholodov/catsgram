package ru.yandex.practicum.catsgram.dal.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.catsgram.model.Image;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ImageRowMapper mapper;

    @Test
    void mapRow_mapsAllFields() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(7L);
        when(resultSet.getLong("post_id")).thenReturn(3L);
        when(resultSet.getString("original_name")).thenReturn("photo.png");
        when(resultSet.getString("file_path")).thenReturn("/images/3/photo.png");

        Image image = mapper.mapRow(resultSet, 0);

        assertThat(image).isNotNull();
        assertThat(image.getId()).isEqualTo(7L);
        assertThat(image.getPostId()).isEqualTo(3L);
        assertThat(image.getOriginalFileName()).isEqualTo("photo.png");
        assertThat(image.getFilePath()).isEqualTo("/images/3/photo.png");
    }
}
