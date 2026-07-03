package ru.yandex.practicum.catsgram.controller.error;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.InternalServerException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFound_returnsErrorResponseWithMessage() {
        NotFoundException ex = new NotFoundException("not found");
        ErrorResponse response = handler.handleNotFound(ex);
        assertThat(response.getError()).isEqualTo("not found");
    }

    @Test
    void handleDuplicatedData_returnsErrorResponseWithMessage() {
        DuplicatedDataException ex = new DuplicatedDataException("duplicate");
        ErrorResponse response = handler.handleDuplicatedData(ex);
        assertThat(response.getError()).isEqualTo("duplicate");
    }

    @Test
    void handleConditionsNotMet_returnsErrorResponseWithMessage() {
        ConditionsNotMetException ex = new ConditionsNotMetException("bad condition");
        ErrorResponse response = handler.handleConditionsNotMet(ex);
        assertThat(response.getError()).isEqualTo("bad condition");
    }

    @Test
    void handleParameterNotValid_returnsFormattedMessage() {
        ParameterNotValidException ex = new ParameterNotValidException("size", "must be positive");
        ErrorResponse response = handler.handleParameterNotValid(ex);
        assertThat(response.getError()).isEqualTo("Некорректное значение параметра size: must be positive");
    }

    @Test
    void handleInternalServer_returnsErrorResponseWithMessage() {
        InternalServerException ex = new InternalServerException("internal error");
        ErrorResponse response = handler.handleInternalServer(ex);
        assertThat(response.getError()).isEqualTo("internal error");
    }

    @Test
    void handleImageFile_returnsFileErrorMessage() {
        ImageFileException ex = new ImageFileException("file error");
        ErrorResponse response = handler.handleImageFile(ex);
        assertThat(response.getError()).isEqualTo("file error");
    }

    @Test
    void handleThrowable_returnsGenericMessage() {
        Throwable ex = new RuntimeException("something unexpected");
        ErrorResponse response = handler.handleThrowable(ex);
        assertThat(response.getError()).isEqualTo("Произошла непредвиденная ошибка.");
    }
}
