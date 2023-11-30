package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("userDbStorage") UserDbStorage userStorage,
                         @Qualifier("filmDbStorage") FilmDbStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review createReview(Review review) {
        userStorage.checkUser(review.getUserId());
        filmStorage.checkFilm(review.getFilmId());

        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        userStorage.checkUser(review.getUserId());
        filmStorage.checkFilm(review.getFilmId());

        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        reviewStorage.deleteReviewById(id);
    }
}
