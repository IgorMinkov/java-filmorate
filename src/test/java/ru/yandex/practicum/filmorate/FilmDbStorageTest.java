package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void filmCrudTest() {
        Film newFilm = makeFilm();
        FilmDbStorage filmStorage = new  FilmDbStorage(jdbcTemplate, null, null);
        filmStorage.createFilm(newFilm);

        Film savedFilm = filmStorage.getFilmById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);

        List<Film> films = filmStorage.getAllFilms();
        Assertions.assertNotNull(films);
        Assertions.assertEquals(1, films.size());

        Film updatedFilm = filmStorage.updateFilm(savedFilm);

        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedFilm);
    }

    private Film makeFilm() {
        MpaRating mpa = new MpaRating(1, "PG-13");
        Set<Genre> genres = new HashSet<>();
        return new Film(null, "Back to the Future Part III",
                "Third part of the legendary movie",
                LocalDate.of(1990, 5, 25),
                118L, mpa, genres, null);
    }

}
