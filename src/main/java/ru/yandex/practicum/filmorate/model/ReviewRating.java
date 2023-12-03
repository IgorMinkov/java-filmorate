package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReviewRating {
    @NotNull
    private final Long reviewId;

    @NotNull
    private final Long userId;

    private final Boolean isPositive;
}
