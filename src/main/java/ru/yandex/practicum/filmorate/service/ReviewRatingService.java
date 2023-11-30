package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.ReviewRating;
import ru.yandex.practicum.filmorate.storage.review.ReviewRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class ReviewRatingService {
    private final ReviewRatingDbStorage reviewRatingDbStorage;
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewRatingService(ReviewRatingDbStorage reviewRatingDbStorage,
                               ReviewStorage reviewStorage,
                               @Qualifier("userDbStorage") UserStorage userStorage) {
        this.reviewRatingDbStorage = reviewRatingDbStorage;
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
    }

    public void create(ReviewRating reviewRating) {
        userStorage.checkUser(reviewRating.getUserId());

        reviewRatingDbStorage.create(reviewRating);
        reviewStorage.recalculateUsefulByReviewId(reviewRating.getReviewId());
    }

    public void delete(ReviewRating reviewRating) {
        userStorage.checkUser(reviewRating.getUserId());

        reviewRatingDbStorage.delete(reviewRating);
        reviewStorage.recalculateUsefulByReviewId(reviewRating.getReviewId());
    }
}
