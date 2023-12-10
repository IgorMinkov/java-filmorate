package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @GetMapping
    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaRatingById(@Positive @PathVariable Integer id) {
        return mpaRatingService.getMpaRatingById(id);
    }

}
