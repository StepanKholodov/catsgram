package ru.yandex.practicum.catsgram.exception;

/**
 * Выбрасывается при ошибках работы с файлом изображения на диске
 * (сохранение, чтение или отсутствие файла).
 */
public class ImageFileException extends RuntimeException {

    /**
     * Создаёт исключение с описанием проблемы и первопричиной.
     *
     * @param message описание проблемы
     * @param cause причина возникновения ошибки
     */
    public ImageFileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создаёт исключение с описанием проблемы.
     *
     * @param message описание проблемы
     */
    public ImageFileException(String message) {
        super(message);
    }
}
