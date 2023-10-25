package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class FilmControllerTests {

    private Film newFilm;
    private final FilmController filmController = new FilmController();

    @BeforeEach
    void setUp() {
        newFilm = createFilm();
    }

    @Test
    public void createAndGetFilmsTest() {
        Film testFilm = filmController.createFilm(newFilm);
        Assertions.assertNotNull(testFilm);
        Assertions.assertEquals(newFilm,testFilm);

        List<Film> filmsList = filmController.getAllFilms();
        Assertions.assertNotNull(filmsList);
    }

    @Test
    public void updateUserTest() {
        Film testFilm = filmController.createFilm(newFilm);
        Film updatedFilm = filmController.updateFilm(testFilm);
        Assertions.assertEquals(testFilm,updatedFilm);

        Assertions.assertThrows(ValidationException.class,
                () -> filmController.updateFilm(null));

        Film unknownFilm = Film.builder()
                .id(-1)
                .name("Движение вверх")
                .description("За себя и за Сашку!")
                .releaseDate(LocalDate.of(2018,6,6))
                .duration(123)
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> filmController.updateFilm(unknownFilm));
    }

    @Test
    void validateFilm() {
        filmController.validateFilm(newFilm);

        newFilm.setReleaseDate(LocalDate.of(1885,9,2));
        Assertions.assertThrows(ValidationException.class,
                () -> filmController.validateFilm(newFilm));
    }

    private Film createFilm() {
        return Film.builder()
                .id(1)
                .name("Back to the Future Part III")
                .description("Third part of the legendary movie")
                .releaseDate(LocalDate.of(1990,5,25))
                .duration(118)
                .build();
    }

}
