package ru.yandex.practicum.catsgram.dto.user;

import lombok.Data;

/**
 * Данные, передаваемые клиентом при регистрации нового пользователя.
 */
@Data
public class NewUserRequest {
    private String username;
    private String email;
    private String password;
}