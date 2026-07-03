package ru.yandex.practicum.catsgram.dto.user;

import lombok.Data;

/**
 * Данные, передаваемые клиентом при обновлении пользователя. Поля необязательны:
 * заполняются только те, что нужно изменить.
 */
@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private String password;

    /**
     * Проверяет, указано ли новое имя пользователя.
     *
     * @return {@code true}, если username не null и не пуст
     */
    public boolean hasUsername() {
        return ! (username == null || username.isBlank());
    }

    /**
     * Проверяет, указан ли новый email.
     *
     * @return {@code true}, если email не null и не пуст
     */
    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    /**
     * Проверяет, указан ли новый пароль.
     *
     * @return {@code true}, если password не null и не пуст
     */
    public boolean hasPassword() {
        return ! (password == null || password.isBlank());
    }
}