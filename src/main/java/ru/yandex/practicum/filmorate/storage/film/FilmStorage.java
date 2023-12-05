package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film getById(Long id);

    List<Film> getPopular(Integer limit);

    void checkFilm(Long id);

    List<Film> getDirectorFilmsSortByYear(long directorId);

    List<Film> getDirectorFilmsSortByLikes(long directorId);
}
