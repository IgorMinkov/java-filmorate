package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewRating;

public interface ReviewRatingStorage {
    ReviewRating create(ReviewRating reviewRating);

    void delete(ReviewRating reviewRating);
}
