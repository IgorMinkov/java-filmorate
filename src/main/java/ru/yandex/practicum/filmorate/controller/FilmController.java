package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> filmStorage = new HashMap<>();
    private long filmCounter = 0;

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(++filmCounter);
        filmStorage.put(film.getId(), film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film == null || !filmStorage.containsKey(film.getId())) {
            log.debug("Фильм не прошел валидацию при обновлении: {}", film);
            throw new ValidationException(
                    String.format("Не найден фильм для обновления: %s", film));
        }
        validateFilm(film);
        filmStorage.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    public void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.debug("Фильм не прошел валидацию: {}", film);
            throw new ValidationException("Невозможная дата премьеры фильма");
        }
    }

}

