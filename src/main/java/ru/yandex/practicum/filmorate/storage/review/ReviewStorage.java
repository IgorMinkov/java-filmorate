package ru.yandex.practicum.filmorate.storage.review;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review createReview(Review review);
}
