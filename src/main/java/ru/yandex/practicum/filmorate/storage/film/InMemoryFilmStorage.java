package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private long filmCounter = 0;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public Film create(Film film) {
        generateFilmId(film);
        filmStorage.put(film.getId(), film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilm(film.getId());
        filmStorage.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public Film getById(Long id) {
        return filmStorage.get(id);
    }

    @Override
    public List<Film> getPopular(Long genreId, String year, Integer limit) {
        return getAll().stream()
                .sorted(Comparator.comparingLong(Film::getLikesCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommon(Long userId, Long friendId) {
        return new ArrayList<>();
    }

    @Override
    public void checkFilm(Long id) {
        if (id == null || !filmStorage.containsKey(id)) {
            throw new DataNotFoundException(
                    String.format("Не найден фильм для обновления: %s", id));
        }
    }

    @Override
    public List<Film> getDirectorFilmsSortByYear(long directorId) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsSortByLikes(long directorId) {
        return null;
    }

    @Override
    public List<Film> getSearchResults(String query, String[] paramsList) {
        return null;
    }

    private void generateFilmId(Film film) {
        film.setId(++filmCounter);
    }

}
