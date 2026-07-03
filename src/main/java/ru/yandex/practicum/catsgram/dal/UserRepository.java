package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для операций с пользователями в БД.
 */
@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(username, email, password, registration_date)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей
     */
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    /**
     * Ищет пользователя по email.
     *
     * @param email адрес электронной почты
     * @return найденный пользователь либо {@link Optional#empty()}
     */
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    /**
     * Ищет пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь либо {@link Optional#empty()}
     */
    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    /**
     * Сохраняет нового пользователя и присваивает ему сгенерированный идентификатор.
     *
     * @param user данные пользователя для сохранения
     * @return сохранённый пользователь с заполненным id
     */
    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Timestamp.from(user.getRegistrationDate())
        );
        user.setId(id);
        return user;
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user пользователь с новыми данными
     * @return обновлённый пользователь
     */
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getId()
        );
        return user;
    }
}
