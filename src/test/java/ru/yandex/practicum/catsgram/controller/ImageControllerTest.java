package ru.yandex.practicum.catsgram.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    void getPostImages_returnsOkWithList() throws Exception {
        ImageDto dto = new ImageDto();
        dto.setId(1L);
        dto.setPostId(10L);
        dto.setOriginalFileName("cat.jpg");

        when(imageService.getPostImages(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/posts/10/images"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].originalFileName").value("cat.jpg"));
    }

    @Test
    void getPostImages_whenPostNotFound_returns404() throws Exception {
        when(imageService.getPostImages(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/posts/99/images"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addPostImages_returnsCreatedWithDto() throws Exception {
        ImageDto dto = new ImageDto();
        dto.setId(5L);
        dto.setPostId(10L);
        dto.setOriginalFileName("photo.jpg");

        when(imageService.saveImages(eq(10L), any())).thenReturn(List.of(dto));

        MockMultipartFile file = new MockMultipartFile(
            "image", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/posts/10/images").file(file))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$[0].originalFileName").value("photo.jpg"));
    }

    @Test
    void downloadImage_whenFound_returnsOctetStream() throws Exception {
        byte[] data = {10, 20, 30};
        ImageData imageData = new ImageData(data, "cat.jpg");

        when(imageService.getImageData(1L)).thenReturn(imageData);

        mockMvc.perform(get("/images/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(content().bytes(data));
    }

    @Test
    void downloadImage_whenNotFound_returns404() throws Exception {
        when(imageService.getImageData(anyLong())).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/images/99"))
            .andExpect(status().isNotFound());
    }
}
