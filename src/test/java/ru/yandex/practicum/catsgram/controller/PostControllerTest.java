package ru.yandex.practicum.catsgram.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.service.PostService;

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

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    void findAll_withDefaultParams_returnsOk() throws Exception {
        PostDto dto = new PostDto();
        dto.setId(1L);
        dto.setAuthorId(1L);
        dto.setDescription("a post");
        dto.setPostDate(Instant.now());

        when(postService.findAll(0, 10, "desc")).thenReturn(List.of(dto));

        mockMvc.perform(get("/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].description").value("a post"));
    }

    @Test
    void findAll_withNegativeSize_returns400() throws Exception {
        mockMvc.perform(get("/posts").param("size", "-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_withZeroSize_returns400() throws Exception {
        mockMvc.perform(get("/posts").param("size", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_withNegativeFrom_returns400() throws Exception {
        mockMvc.perform(get("/posts").param("from", "-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_withInvalidSort_returns400() throws Exception {
        mockMvc.perform(get("/posts").param("sort", "invalid"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsCreatedStatus() throws Exception {
        NewPostRequest request = new NewPostRequest();
        request.setAuthorId(1L);
        request.setDescription("New cat photo");

        PostDto dto = new PostDto();
        dto.setId(10L);
        dto.setAuthorId(1L);
        dto.setDescription("New cat photo");
        dto.setPostDate(Instant.now());

        when(postService.create(any(NewPostRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value("New cat photo"));
    }

    @Test
    void update_returnsOk() throws Exception {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("updated description");

        PostDto dto = new PostDto();
        dto.setId(1L);
        dto.setAuthorId(1L);
        dto.setDescription("updated description");
        dto.setPostDate(Instant.now());

        when(postService.update(eq(1L), any(UpdatePostRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("updated description"));
    }

    @Test
    void getById_whenFound_returnsOk() throws Exception {
        PostDto dto = new PostDto();
        dto.setId(5L);
        dto.setAuthorId(2L);
        dto.setDescription("Found post");
        dto.setPostDate(Instant.now());

        when(postService.findById(5L)).thenReturn(dto);

        mockMvc.perform(get("/posts/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Found post"));
    }

    @Test
    void getById_whenNotFound_returns404() throws Exception {
        when(postService.findById(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/posts/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void findAll_withAscSort_returnsOk() throws Exception {
        when(postService.findAll(0, 10, "asc")).thenReturn(List.of());

        mockMvc.perform(get("/posts").param("sort", "asc"))
            .andExpect(status().isOk());
    }
}
