package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.catsgram.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Базовый класс для JDBC-репозиториев, инкапсулирующий общие операции чтения и записи.
 *
 * @param <T> тип сущности, с которой работает репозиторий
 */
@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    /**
     * Выполняет запрос, ожидая не более одной строки результата.
     *
     * @param query SQL-запрос
     * @param params параметры запроса
     * @return найденная сущность или {@link Optional#empty()}, если строк нет
     */
    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Выполняет запрос, возвращающий произвольное количество строк.
     *
     * @param query SQL-запрос
     * @param params параметры запроса
     * @return список найденных сущностей
     */
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    /**
     * Удаляет строку по идентификатору.
     *
     * @param query SQL-запрос удаления
     * @param id идентификатор удаляемой строки
     * @return {@code true}, если была удалена хотя бы одна строка
     */
    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    /**
     * Выполняет обновление и проверяет, что затронута хотя бы одна строка.
     *
     * @param query SQL-запрос обновления
     * @param params параметры запроса
     * @throws InternalServerException если ни одна строка не была обновлена
     */
    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    /**
     * Выполняет вставку и возвращает сгенерированный идентификатор.
     *
     * @param query SQL-запрос вставки
     * @param params параметры запроса
     * @return идентификатор вставленной строки
     * @throws InternalServerException если идентификатор не был сгенерирован
     */
    protected long insert(String query, Object... params)  {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
