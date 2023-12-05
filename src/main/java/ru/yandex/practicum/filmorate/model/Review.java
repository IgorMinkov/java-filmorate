package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Review {
    @Positive
    private Long reviewId;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    @NotBlank
    @Size(max = 255)
    private String content;

    @NotNull
    private Boolean isPositive;

    @Builder.Default
    private Integer useful = 0;
}
