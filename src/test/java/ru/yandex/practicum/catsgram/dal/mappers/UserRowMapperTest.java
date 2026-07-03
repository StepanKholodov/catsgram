package ru.yandex.practicum.catsgram.dal.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserRowMapper mapper;

    @Test
    void mapRow_mapsAllFields() throws SQLException {
        Instant now = Instant.now();
        Timestamp ts = Timestamp.from(now);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("username")).thenReturn("alice");
        when(resultSet.getString("email")).thenReturn("alice@example.com");
        when(resultSet.getString("password")).thenReturn("hashed");
        when(resultSet.getTimestamp("registration_date")).thenReturn(ts);

        User user = mapper.mapRow(resultSet, 0);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
        assertThat(user.getPassword()).isEqualTo("hashed");
        assertThat(user.getRegistrationDate()).isEqualTo(now);
    }
}
