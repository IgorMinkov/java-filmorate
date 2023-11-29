package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    List<Genre> getAll();

    Genre getById(Integer id);

    Set<Genre> getFilmGenres(Long filmId);

    void updateFilmGenres(Film film);

}
