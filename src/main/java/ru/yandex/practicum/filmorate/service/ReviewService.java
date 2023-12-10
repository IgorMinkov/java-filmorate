package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
                         UserDbStorage userStorage,
                         FilmDbStorage filmStorage
    ) {
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

    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getAllReviews(Integer count) {
        return reviewStorage.getAllReviews(count);
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        filmStorage.checkFilm(filmId);
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }
}
