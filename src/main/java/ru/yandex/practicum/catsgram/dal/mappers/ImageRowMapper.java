package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.Image;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Преобразует строку {@link ResultSet} таблицы {@code image_storage} в модель {@link Image}.
 */
@Component
public class ImageRowMapper implements RowMapper<Image> {

    /**
     * Читает текущую строку результата и создаёт из неё изображение.
     *
     * @param rs результат SQL-запроса
     * @param rowNum номер строки
     * @return созданное изображение
     * @throws SQLException при ошибке чтения данных из {@link ResultSet}
     */
    @Nullable
    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        Image image = new Image();
        image.setId(rs.getLong("id"));
        image.setPostId(rs.getLong("post_id"));
        image.setOriginalFileName(rs.getString("original_name"));
        image.setFilePath(rs.getString("file_path"));

        return image;
    }
}
