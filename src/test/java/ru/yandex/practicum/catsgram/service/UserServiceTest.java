package ru.yandex.practicum.catsgram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.user.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UserDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_whenEmailIsNull_throwsConditionsNotMetException() {
        NewUserRequest request = new NewUserRequest();
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConditionsNotMetException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenEmailIsEmpty_throwsConditionsNotMetException() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("");
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConditionsNotMetException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsDuplicatedDataException() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("taken@example.com");
        User existing = new User();
        existing.setEmail("taken@example.com");
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(existing));
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicatedDataException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenValidRequest_encodesPasswordAndSaves() {
        NewUserRequest request = new NewUserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("$2a$encoded");
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("alice");
        savedUser.setEmail("alice@example.com");
        savedUser.setPassword("$2a$encoded");
        savedUser.setRegistrationDate(Instant.now());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        UserDto result = userService.createUser(request);
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_whenUserNotFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUserById_whenUserExists_returnsDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setRegistrationDate(Instant.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserById(1L);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("bob");
    }

    @Test
    void getUsers_returnsMappedList() {
        User user = new User();
        user.setId(1L);
        user.setUsername("carol");
        user.setEmail("carol@example.com");
        user.setRegistrationDate(Instant.now());
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> result = userService.getUsers();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("carol");
    }

    @Test
    void getUsers_whenEmpty_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());
        assertThat(userService.getUsers()).isEmpty();
    }

    @Test
    void updateUser_whenUserNotFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(99L, new UpdateUserRequest()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUser_whenPasswordProvided_encodesPassword() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("u@example.com");
        existing.setPassword("oldhash");
        existing.setRegistrationDate(Instant.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$newhash");
        when(userRepository.update(any(User.class))).thenReturn(existing);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("newpass");
        userService.updateUser(1L, request);
        verify(passwordEncoder).encode("newpass");
    }

    @Test
    void updateUser_whenPasswordNotProvided_doesNotEncodePassword() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("u@example.com");
        existing.setPassword("keephash");
        existing.setRegistrationDate(Instant.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.update(any(User.class))).thenReturn(existing);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newname");
        userService.updateUser(1L, request);
        verify(passwordEncoder, never()).encode(anyString());
    }
}
