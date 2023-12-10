package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipStorageDAO implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("пользователь с id {} добавил друга с id {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        try {
            String sqlQuery = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                    "FROM users u " +
                    "LEFT JOIN friendship f ON u.user_id = f.friend_id " +
                    "WHERE f.user_id = ? ";

            return jdbcTemplate.query(sqlQuery, UserDbStorage::buildUser, userId);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        try {
        String sqlQuery = " SELECT *" +
                " FROM users " +
                " WHERE user_id in (SELECT friend_id " +
                " FROM friendship " +
                " WHERE friend_id in (SELECT friend_id " +
                " FROM friendship " +
                " WHERE user_id = ?) " +
                " AND user_id = ?);";

            return jdbcTemplate.query(sqlQuery, UserDbStorage::buildUser, id, otherId);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("пользователь с id {} удалил друга с id {}", userId, friendId);
    }

}
