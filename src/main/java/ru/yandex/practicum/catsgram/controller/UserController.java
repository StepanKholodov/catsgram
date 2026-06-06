package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (users.values()
                .stream()
                .anyMatch(user -> Objects.equals(user.getEmail(), newUser.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        newUser.setId(getNextId());
        newUser.setRegistrationDate(Instant.now());

        users.put(newUser.getId(), newUser);

        return newUser;


    }

    @PutMapping
    public User update(@RequestBody User updateUser) {
        if (updateUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (users.values()
                .stream()
                .anyMatch(user -> Objects.equals(user.getEmail(), updateUser.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (users.containsKey(updateUser.getId())) {
            User oldUser = users.get(updateUser.getId());

            if (updateUser.getEmail() != null) {
                oldUser.setEmail(updateUser.getEmail());
            }
            if (updateUser.getPassword() != null) {
                oldUser.setPassword(updateUser.getPassword());
            }
            if (updateUser.getUsername() != null) {
                oldUser.setUsername(updateUser.getUsername());
            }

            return oldUser;
        }
        throw new NotFoundException("Пост с id = " + updateUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
