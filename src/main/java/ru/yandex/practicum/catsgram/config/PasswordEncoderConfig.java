package ru.yandex.practicum.catsgram.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация бинов, связанных с хешированием паролей.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Создаёт кодировщик паролей на основе BCrypt.
     *
     * @return бин {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
