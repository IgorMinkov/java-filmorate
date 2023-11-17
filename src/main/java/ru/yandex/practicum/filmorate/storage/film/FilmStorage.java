package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getPopularFilms(Integer limit);

    void checkFilm(Long id);

}
