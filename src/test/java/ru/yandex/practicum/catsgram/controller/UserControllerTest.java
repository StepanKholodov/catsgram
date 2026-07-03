package ru.yandex.practicum.catsgram.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.catsgram.dto.user.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.user.UserDto;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.service.UserService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_returnsCreatedStatus() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setRegistrationDate(Instant.now());

        when(userService.createUser(any(NewUserRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getUsers_returnsOkWithList() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("bob");
        dto.setEmail("bob@example.com");
        dto.setRegistrationDate(Instant.now());

        when(userService.getUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("bob"));
    }

    @Test
    void getUserById_whenFound_returnsOk() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("carol");
        dto.setEmail("carol@example.com");
        dto.setRegistrationDate(Instant.now());

        when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("carol"));
    }

    @Test
    void getUserById_whenNotFound_returns404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/users/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_returnsOkWithUpdatedUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updated");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("updated");
        dto.setEmail("u@example.com");
        dto.setRegistrationDate(Instant.now());

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    void updateUser_whenNotFound_returns404() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("new");

        when(userService.updateUser(eq(99L), any(UpdateUserRequest.class)))
            .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(put("/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }
}
