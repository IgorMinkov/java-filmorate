package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users GROUP BY user_id";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::buildUser);
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        checkUser(user.getId());
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUser(user.getId());
        String sqlQuery = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT * FROM users u WHERE u.user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::buildUser, id).stream().findAny()
                .orElseThrow(() -> new DataNotFoundException(String.format("не найден фильм с id %s", id)));
    }

    @Override
    public void checkUser(Long id) {
        try {
            User user = getUserById(id);
            if (user == null) {
                throw new DataNotFoundException(String.format("не найден пользователь с id %s", id));
            }
            log.trace("check user id: {} - OK", id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("Ошибка SQL- в БД нет пользователя с id %s", id));
        }
    }

    private static User buildUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

}
