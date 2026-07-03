package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.user.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UserDto;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;

/**
 * Преобразует между моделью {@link User} и её DTO-представлениями.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    /**
     * Создаёт нового пользователя из данных запроса на регистрацию.
     *
     * @param request данные нового пользователя
     * @return новая модель пользователя с текущей датой регистрации
     */
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRegistrationDate(Instant.now());

        return user;
    }

    /**
     * Преобразует модель пользователя в DTO для ответа клиенту.
     *
     * @param user модель пользователя
     * @return DTO пользователя
     */
    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }

    /**
     * Применяет к существующему пользователю переданные в запросе поля.
     *
     * @param user обновляемый пользователь
     * @param request данные для обновления
     * @return тот же пользователь с обновлёнными полями
     */
    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasUsername()) {
            user.setUsername(request.getUsername());
        }
        return user;
    }
}