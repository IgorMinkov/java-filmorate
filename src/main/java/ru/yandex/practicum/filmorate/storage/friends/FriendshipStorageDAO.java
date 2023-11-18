package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@Qualifier
@RequiredArgsConstructor
public class FriendshipStorageDAO implements  FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("пользователь с id {} добавил друга с id {}", userId, friendId);
    }

    @Override
    public Set<Long> getUserFriends(Long userId) {
        Set<Long> friends = new HashSet<>();
        try {
            String sqlQuery = "SELECT friend_id FROM friendship WHERE user_id = ?";
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
            while (friendsRows.next()) {
                friends.add(friendsRows.getLong("friend_id"));
            }
            return friends;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptySet();
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("пользователь с id {} удалил друга с id {}", userId, friendId);
    }

}
