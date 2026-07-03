package ru.yandex.practicum.catsgram.controller.error;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.catsgram.exception.*;

/**
 * Централизованный обработчик исключений, переводящий их в HTTP-ответы с телом {@link ErrorResponse}.
 */
@Log4j2
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Обрабатывает случай, когда запрашиваемая сущность не найдена.
     *
     * @param e исключение с описанием того, что не найдено
     * @return тело ответа с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает конфликт уникальности данных (например, занятый email).
     *
     * @param e исключение с описанием конфликта
     * @return тело ответа с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedData(final DuplicatedDataException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает нарушение обязательных условий выполнения операции.
     *
     * @param e исключение с описанием нарушенного условия
     * @return тело ответа с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleConditionsNotMet(final ConditionsNotMetException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает некорректное значение параметра запроса.
     *
     * @param e исключение с именем параметра и причиной ошибки
     * @return тело ответа с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterNotValid(final ParameterNotValidException e) {
        return new ErrorResponse("Некорректное значение параметра " + e.getParameter() + ": " + e.getReason());
    }

    /**
     * Обрабатывает внутренние ошибки приложения, логируя стектрейс.
     *
     * @param e исключение с описанием внутренней ошибки
     * @return тело ответа с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServer(final InternalServerException e) {
        log.error("Внутренняя ошибка приложения", e);
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает ошибки работы с файлом изображения.
     *
     * @param e исключение с описанием ошибки файла
     * @return тело ответа с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleImageFile(final ImageFileException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает все остальные непредвиденные ошибки, логируя стектрейс.
     *
     * @param e непредвиденное исключение
     * @return тело ответа с общим сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Непредвиденная ошибка", e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
