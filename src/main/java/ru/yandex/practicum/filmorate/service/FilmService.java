package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    private final LikesStorage likesStorage;

    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserService userService, LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likesStorage = likesStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new ValidationException("в метод передан null");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Невозможная дата премьеры фильма");
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long id, Long userId) {
        validateFilm(id);
        userService.validateUser(userId);
        likesStorage.addLike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        validateFilm(id);
        userService.validateUser(userId);
        likesStorage.removeLike(id, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null || count <= 0) {
            throw new ValidationException(
                    String.format("в метод getPopularFilms передан некорретный параметр: %d", count));
        }
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Long id) {
        filmStorage.checkFilm(id);
    }

}
