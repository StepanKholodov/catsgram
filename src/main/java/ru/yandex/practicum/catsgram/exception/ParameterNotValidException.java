package ru.yandex.practicum.catsgram.exception;

import lombok.Getter;

/**
 * Выбрасывается, когда значение конкретного параметра запроса некорректно.
 */
@Getter
public class ParameterNotValidException extends IllegalArgumentException {
    private final String parameter;
    private final String reason;

    /**
     * Создаёт исключение с указанием параметра и причины его некорректности.
     *
     * @param parameter имя некорректного параметра
     * @param reason причина, по которой параметр считается некорректным
     */
    public ParameterNotValidException(String parameter, String reason) {
        super(reason);
        this.parameter = parameter;
        this.reason = reason;
    }
}
