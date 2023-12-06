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
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
        log.info("пользователю с id {} нравится фильм с id {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public List<Long> getLikedFilmsId(Long userId) {
        String sqlQuery = "SELECT film_id FROM Likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    @Override
    public Long getSameLikesUserId(Long userId) {
        String sqlQuery = "SELECT l2.user_Id " +
                "FROM likes AS l1 " +
                "JOIN likes AS l2 ON l1.film_id = l2.film_id " +
                "WHERE l1.user_id = ? AND l1.user_id<>l2.user_id " +
                "GROUP BY l2.user_id " +
                "ORDER BY COUNT(l2.user_id) DESC " +
                "LIMIT 1";

        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId).stream()
                .findFirst().orElse(null);
    }

}
