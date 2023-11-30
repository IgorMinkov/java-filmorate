package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    List<Director> getAll();

    Set<Director> getByFilmId(long filmId);

    Director getById(long directorId);

    Director createDirector(Director director);

    void updateFilmDirectors(Film film);

    Director updateDirector(Director director);

    void deleteDirector(long directorId);
}
