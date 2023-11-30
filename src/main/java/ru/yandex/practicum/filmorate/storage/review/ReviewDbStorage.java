package ru.yandex.practicum.filmorate.storage.review;

import java.sql.PreparedStatement;
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
}
