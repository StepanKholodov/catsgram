package ru.yandex.practicum.catsgram.dto;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;

import static org.assertj.core.api.Assertions.assertThat;

class UpdatePostRequestTest {

    @Test
    void hasDescription_whenDescriptionIsNull_returnsFalse() {
        UpdatePostRequest request = new UpdatePostRequest();
        assertThat(request.hasDescription()).isFalse();
    }

    @Test
    void hasDescription_whenDescriptionIsBlank_returnsFalse() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("   ");
        assertThat(request.hasDescription()).isFalse();
    }

    @Test
    void hasDescription_whenDescriptionIsEmpty_returnsFalse() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("");
        assertThat(request.hasDescription()).isFalse();
    }

    @Test
    void hasDescription_whenDescriptionIsPresent_returnsTrue() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setDescription("A lovely cat photo");
        assertThat(request.hasDescription()).isTrue();
    }
}
