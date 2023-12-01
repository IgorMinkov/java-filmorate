package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    List<Director> getAll();

    Set<Director> getByFilmId(Long filmId);

    Director getById(Long directorId);

    Director createDirector(Director director);

    void updateFilmDirectors(Film film);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);
}
