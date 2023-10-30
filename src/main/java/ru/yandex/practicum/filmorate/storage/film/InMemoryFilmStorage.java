package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
        filmStorage.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(filmStorage.get(id));
    }

    private void generateFilmId(Film film) {
        film.setId(++filmCounter);
    }

}
