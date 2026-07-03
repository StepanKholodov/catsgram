package ru.yandex.practicum.catsgram.exception;

/**
 * Выбрасывается, когда обязательные условия для выполнения операции не соблюдены
 * (например, не указано необходимое поле запроса).
 */
public class ConditionsNotMetException extends RuntimeException {

    /**
     * Создаёт исключение с описанием нарушенного условия.
     *
     * @param message описание проблемы
     */
    public ConditionsNotMetException(String message) {
        super(message);
    }
}
