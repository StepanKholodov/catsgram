package ru.yandex.practicum.catsgram.dto;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateUserRequestTest {

    @Test
    void hasEmail_whenEmailIsNull_returnsFalse() {
        UpdateUserRequest request = new UpdateUserRequest();
        assertThat(request.hasEmail()).isFalse();
    }

    @Test
    void hasEmail_whenEmailIsBlank_returnsFalse() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("   ");
        assertThat(request.hasEmail()).isFalse();
    }

    @Test
    void hasEmail_whenEmailIsPresent_returnsTrue() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("user@example.com");
        assertThat(request.hasEmail()).isTrue();
    }

    @Test
    void hasUsername_whenUsernameIsNull_returnsFalse() {
        UpdateUserRequest request = new UpdateUserRequest();
        assertThat(request.hasUsername()).isFalse();
    }

    @Test
    void hasUsername_whenUsernameIsBlank_returnsFalse() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("  ");
        assertThat(request.hasUsername()).isFalse();
    }

    @Test
    void hasUsername_whenUsernameIsPresent_returnsTrue() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("alice");
        assertThat(request.hasUsername()).isTrue();
    }

    @Test
    void hasPassword_whenPasswordIsNull_returnsFalse() {
        UpdateUserRequest request = new UpdateUserRequest();
        assertThat(request.hasPassword()).isFalse();
    }

    @Test
    void hasPassword_whenPasswordIsBlank_returnsFalse() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("");
        assertThat(request.hasPassword()).isFalse();
    }

    @Test
    void hasPassword_whenPasswordIsPresent_returnsTrue() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("secret");
        assertThat(request.hasPassword()).isTrue();
    }
}
