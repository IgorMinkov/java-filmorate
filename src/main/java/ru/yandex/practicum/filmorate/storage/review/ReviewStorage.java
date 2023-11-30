package ru.yandex.practicum.filmorate.storage.review;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    Review getReviewById(long id);

    void deleteReviewById(long id);

    List<Review> getAllReviews(int count);

    List<Review> getReviewsByFilmId(long filmId, int count);

    void recalculateUsefulByReviewId(long reviewId);
}
