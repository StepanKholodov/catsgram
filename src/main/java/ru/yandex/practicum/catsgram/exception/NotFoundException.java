package ru.yandex.practicum.catsgram.exception;

/**
 * Выбрасывается, когда запрашиваемая сущность (пользователь, пост, изображение)
 * не найдена.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с описанием того, что не найдено.
     *
     * @param message описание проблемы
     */
    public NotFoundException(String message) {
        super(message);
    }
}
