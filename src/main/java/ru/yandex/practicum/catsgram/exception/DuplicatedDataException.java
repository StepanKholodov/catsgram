package ru.yandex.practicum.catsgram.exception;

/**
 * Выбрасывается при попытке сохранить данные, нарушающие требование уникальности
 * (например, email, который уже используется другим пользователем).
 */
public class DuplicatedDataException extends RuntimeException {

    /**
     * Создаёт исключение с описанием конфликта данных.
     *
     * @param message описание проблемы
     */
    public DuplicatedDataException(String message) {
        super(message);
    }
}
