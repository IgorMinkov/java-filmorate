package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewRating;
import ru.yandex.practicum.filmorate.service.ReviewRatingService;
import ru.yandex.practicum.filmorate.service.ReviewService;

@Slf4j
@RestController
@Validated
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRatingService reviewRatingService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на добавление нового отзыва: {}", review);

        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление имеющегося отзыва: {}", review);

        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@Positive @PathVariable Long id) {
        log.info("Получен запрос на удаление отзыва по id: {}", id);

        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@Positive @PathVariable Long id) {
        log.info("Получен запрос на загрузку отзыва по id: {}", id);

        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Long filmId, @Positive @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получен запрос на загрузку {} отзывов фильма по id: {}", count, filmId);

        if (filmId == null) {
            return reviewService.getAllReviews(count);
        }

        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на добавление лайка к отзыву: {} от пользователя: {}", id, userId);

        reviewRatingService.create(ReviewRating.builder()
                        .reviewId(id)
                        .userId(userId)
                        .isPositive(true)
                        .build());
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на добавление дизлайка к отзыву: {} от пользователя: {}", id, userId);

        reviewRatingService.create(ReviewRating.builder()
                .reviewId(id)
                .userId(userId)
                .isPositive(false)
                .build());
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка из отзыва: {} от пользователя: {}", id, userId);

        reviewRatingService.delete(ReviewRating.builder()
                .reviewId(id)
                .userId(userId)
                .isPositive(true)
                .build());
    }
}
