package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.user.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UserDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для операций с пользователями: создание, поиск и обновление.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Создаёт пользователя, если его email ещё не занят.
     *
     * @param request данные нового пользователя
     * @return созданный пользователь
     */
    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user = userRepository.save(user);
        log.info("Создан пользователь id={}, email={}", user.getId(), user.getEmail());

        return UserMapper.mapToUserDto(user);
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     */
    public UserDto getUserById(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей
     */
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    /**
     * Обновляет пользователя. Если передан новый email, проверяет, что он не занят другим пользователем.
     *
     * @param userId идентификатор пользователя
     * @param request новые данные пользователя
     * @return обновлённый пользователь
     * @throws NotFoundException если пользователь с таким id не найден
     * @throws DuplicatedDataException если новый email уже используется другим пользователем
     */
    public UserDto updateUser(long userId, UpdateUserRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (request.hasEmail() && !request.getEmail().equals(existingUser.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                throw new DuplicatedDataException("Данный имейл уже используется");
            });
        }

        User updatedUser = UserMapper.updateUserFields(existingUser, request);
        if (request.hasPassword()) {
            updatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        updatedUser = userRepository.update(updatedUser);
        log.info("Обновлён пользователь id={}", userId);
        return UserMapper.mapToUserDto(updatedUser);
    }
}
