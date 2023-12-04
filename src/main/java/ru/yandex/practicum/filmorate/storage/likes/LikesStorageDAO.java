package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Qualifier("likesStorageDAO")
@RequiredArgsConstructor
public class LikesStorageDAO implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long userId, Long filmId) {
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
        log.info("пользователю с id {} нравится фильм с id {}", userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public List<Long> getLikedFilmsId(Long userId) {
        String sqlQuery = "SELECT film_id FROM Likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

}
