package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private long filmCounter = 0;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public Film createFilm(Film film) {
        generateFilmId(film);
        filmStorage.put(film.getId(), film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilm(film.getId());
        filmStorage.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.get(id);
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingLong(Film::getLikesCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void checkFilm(Long id) {
        if (id == null || !filmStorage.containsKey(id)) {
            throw new DataNotFoundException(
                    String.format("Не найден фильм для обновления: %s", id));
        }
    }

    private void generateFilmId(Film film) {
        film.setId(++filmCounter);
    }

}
