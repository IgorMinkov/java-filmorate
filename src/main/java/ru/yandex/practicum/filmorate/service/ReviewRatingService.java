package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.ReviewRating;
import ru.yandex.practicum.filmorate.storage.review.ReviewRatingStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class ReviewRatingService {
    private final ReviewRatingStorage reviewRatingStorage;
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewRatingService(ReviewRatingStorage reviewRatingStorage,
                               ReviewStorage reviewStorage,
                               UserStorage userStorage
    ) {
        this.reviewRatingStorage = reviewRatingStorage;
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
    }

    public void create(ReviewRating reviewRating) {
        userStorage.checkUser(reviewRating.getUserId());

        reviewRatingStorage.create(reviewRating);
        reviewStorage.recalculateUsefulByReviewId(reviewRating.getReviewId());
    }

    public void delete(ReviewRating reviewRating) {
        userStorage.checkUser(reviewRating.getUserId());

        reviewRatingStorage.delete(reviewRating);
        reviewStorage.recalculateUsefulByReviewId(reviewRating.getReviewId());
    }
}
