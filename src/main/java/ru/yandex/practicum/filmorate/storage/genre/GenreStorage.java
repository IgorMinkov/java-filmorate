package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(Integer id);

    Set<Genre> getGenresOfFilm(Long filmId);

    void updateGenresOfFilm(Film film);

}
