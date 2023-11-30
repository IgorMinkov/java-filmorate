package ru.yandex.practicum.filmorate.storage.review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        String sqlQuery = "INSERT INTO reviews (user_id, film_id, content, is_positive) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
                statement.setLong(1, review.getUserId());
                statement.setLong(2, review.getFilmId());
                statement.setString(3, review.getContent());
                statement.setBoolean(4, review.getIsPositive());

                return statement;
            }, keyHolder);

        } catch (DataIntegrityViolationException e) {
            throw new DataNotFoundException(e.getMessage());
        }

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        log.info("Создан новый отзыв: {}", review);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        review = getReviewById(review.getReviewId());

        log.info("Обновлен существующий отзыв: {}", review);
        return review;
    }

    @Override
    public Review getReviewById(long id) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ? ORDER BY useful DESC";

        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::buildReview, id).stream()
                .findAny()
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найден отзыв с id %s", id)));
    }

    public static Review buildReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
