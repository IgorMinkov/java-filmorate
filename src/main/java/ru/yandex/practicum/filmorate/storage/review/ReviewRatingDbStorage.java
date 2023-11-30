package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.ReviewRating;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewRatingDbStorage implements ReviewRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ReviewRating create(ReviewRating reviewRating) {
        String sqlQuery = "INSERT INTO review_rating (review_id, user_id, is_positive) VALUES (?, ?, ?)";

        jdbcTemplate.update(sqlQuery, reviewRating.getReviewId(), reviewRating.getUserId(), reviewRating.getIsPositive());

        log.info("Добавлена новая оценка к отзыву: {}", reviewRating);

        return reviewRating;
    }

    @Override
    public void delete(ReviewRating reviewRating) {
        String sqlQuery = "DELETE FROM review_rating WHERE review_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, reviewRating.getReviewId(), reviewRating.getUserId());

        log.info("Удалёна оценка из отзыва: {}", reviewRating);
    }
}
