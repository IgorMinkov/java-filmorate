package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new ValidationException("в метод передан null");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Невозможная дата премьеры фильма");
        }
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film.getId());
        return filmStorage.update(film);
    }

    public void delete(Long filmId) {
        validateFilm(filmId);
        filmStorage.delete(filmId);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id);
    }

    public void addLike(Long filmId, Long userId) {
        validateFilm(filmId);
        userService.validateUser(userId);
        likesStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilm(filmId);
        userService.validateUser(userId);
        likesStorage.removeLike(filmId, userId);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userService.validateUser(userId);
        userService.validateUser(friendId);
        return filmStorage.getCommon(userId, friendId);
    }

    public List<Film> getSortedFilmByDirector(Long directorId, String sortMethod) {
        List<Film> films;
        switch (sortMethod.toLowerCase()) {
            case "year":
                films = filmStorage.getDirectorFilmsSortByYear(directorId);
                break;
            case "likes":
                films = filmStorage.getDirectorFilmsSortByLikes(directorId);
                break;
            default:
                throw new DataNotFoundException("Данный метод сортировки не поддерживается");
        }
        if (films.isEmpty()) {
            throw new DataNotFoundException("Отсутствуют фильмы указанного режиссера");
        }
        return films;
    }

    public List<Film> getRecommendations(Long userId) {
        userService.validateUser(userId);
        List<Long> userFilms = likesStorage.getLikedFilmsId(userId);
        if (userFilms.isEmpty()) {
            return new ArrayList<>();
        }

        Long sameTasteUserId = likesStorage.getSameLikesUserId(userId);
        if (sameTasteUserId == null) {
            return List.of();
        }

        return likesStorage.getLikedFilmsId(sameTasteUserId).stream()
                .filter(filmId -> !userFilms.contains(filmId))
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    private void validateFilm(Long id) {
        filmStorage.checkFilm(id);
    }

    public List<Film> getPopularFilms(Long genreId, String year, Integer limit) {
        if (limit == null || limit <= 0) {
            throw new ValidationException(
                    String.format("в метод getPopularFilms передан некорректный параметр: %d", limit));
        }
            return filmStorage.getPopular(genreId, year, limit);
    }

    public List<Film> getSearchResults(String query, String params) {
        String[] paramsList = params.split(",");
        return filmStorage.getSearchResults(query, paramsList);
    }

}
