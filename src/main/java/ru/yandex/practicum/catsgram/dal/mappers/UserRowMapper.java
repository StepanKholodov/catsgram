package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Преобразует строку {@link ResultSet} таблицы {@code users} в модель {@link User}.
 */
@Component
public class UserRowMapper implements RowMapper<User> {

    /**
     * Читает текущую строку результата и создаёт из неё пользователя.
     *
     * @param resultSet результат SQL-запроса
     * @param rowNum номер строки
     * @return созданный пользователь
     * @throws SQLException при ошибке чтения данных из {@link ResultSet}
     */
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));

        Timestamp registrationDate = resultSet.getTimestamp("registration_date");
        user.setRegistrationDate(registrationDate.toInstant());

        return user;
    }
}