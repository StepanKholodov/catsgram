package ru.yandex.practicum.catsgram.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.catsgram.dto.user.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UserDto;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void mapToUser_setsAllFields() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("pass123");
        User user = UserMapper.mapToUser(request);
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
        assertThat(user.getPassword()).isEqualTo("pass123");
        assertThat(user.getRegistrationDate()).isNotNull();
    }

    @Test
    void mapToUserDto_mapsAllFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setRegistrationDate(Instant.now());
        UserDto dto = UserMapper.mapToUserDto(user);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
        assertThat(dto.getRegistrationDate()).isEqualTo(user.getRegistrationDate());
    }

    @Test
    void updateUserFields_updatesEmailWhenPresent() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setUsername("oldname");
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@example.com");
        User updated = UserMapper.updateUserFields(user, request);
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getUsername()).isEqualTo("oldname");
    }

    @Test
    void updateUserFields_updatesUsernameWhenPresent() {
        User user = new User();
        user.setUsername("oldname");
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newname");
        User updated = UserMapper.updateUserFields(user, request);
        assertThat(updated.getUsername()).isEqualTo("newname");
    }

    @Test
    void updateUserFields_doesNotUpdateEmailWhenBlank() {
        User user = new User();
        user.setEmail("keep@example.com");
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("  ");
        User updated = UserMapper.updateUserFields(user, request);
        assertThat(updated.getEmail()).isEqualTo("keep@example.com");
    }

    @Test
    void updateUserFields_doesNotUpdateUsernameWhenNull() {
        User user = new User();
        user.setUsername("keepname");
        UpdateUserRequest request = new UpdateUserRequest();
        User updated = UserMapper.updateUserFields(user, request);
        assertThat(updated.getUsername()).isEqualTo("keepname");
    }
}
