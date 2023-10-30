package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new ValidationException("в метод передан null");
        }
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film == null || !filmStorage.getAllFilms().contains(film)) {
            throw new DataNotFoundException(
                    String.format("Не найден фильм для обновления: %s", film));
        }
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id).orElseThrow(() ->
                new DataNotFoundException(
                        String.format("Не найден фильм c id: %s", id)));
    }

    public void addLike(Long id, Long userId) {
        Film film = getFilmById(id);
        film.getLikes().add(userService.getUserById(userId).getId());
        updateFilm(film);
    }

    public void removeLike(Long id, Long userId) {
        Film film = getFilmById(id);
        film.getLikes().remove(userService.getUserById(userId).getId());
        updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null || count <= 0) {
            throw new ValidationException(
                    String.format("в метод getPopularFilms передан некорретный параметр: %d", count));
        }
        return getAllFilms().stream()
                .sorted(Comparator.comparingLong(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Невозможная дата премьеры фильма");
        }
    }

}
