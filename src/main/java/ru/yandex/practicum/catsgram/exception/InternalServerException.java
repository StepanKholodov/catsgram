package ru.yandex.practicum.catsgram.exception;

/**
 * Выбрасывается при внутренних сбоях приложения, не связанных с некорректными
 * входными данными (например, неудачная операция в БД).
 */
public class InternalServerException extends RuntimeException {

    /**
     * Создаёт исключение с описанием внутренней ошибки.
     *
     * @param message описание проблемы
     */
    public InternalServerException(String message) {
        super(message);
    }
}
