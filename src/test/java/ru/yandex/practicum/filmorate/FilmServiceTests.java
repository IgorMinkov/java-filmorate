package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
public class FilmServiceTests {

    private Film newFilm;
    private Film testFilm;

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmServiceTests(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        newFilm = createFilm();
        testFilm = filmService.createFilm(newFilm);
    }

    @Test
    public void createAndGetFilmsTest() {
        Assertions.assertNotNull(testFilm);
        Assertions.assertEquals(newFilm, testFilm);

        final List<Film> filmsList = filmService.getAllFilms();
        Assertions.assertNotNull(filmsList);

        newFilm.setReleaseDate(LocalDate.of(1885, 9, 2));
        Assertions.assertThrows(ValidationException.class,
                () -> filmService.createFilm(newFilm));
    }

    @Test
    public void updateFilmTest() {
        Film updatedFilm = filmService.updateFilm(testFilm);
        Assertions.assertEquals(testFilm, updatedFilm);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.updateFilm(null));

        Film unknownFilm = new Film(-1L, "Движение вверх", "За себя и за Сашку!",
                LocalDate.of(2018, 6, 6), 123L,
                null, new HashSet<>(), new HashSet<>());

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.updateFilm(unknownFilm));
    }

    @Test
    public void getFilmByIdTest() {
        final Film film = filmService.getFilmById(testFilm.getId());
        Assertions.assertNotNull(film);
        Assertions.assertEquals(testFilm, film);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.getFilmById(null));

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.getFilmById(999L));
    }

    @Test
    public void addLikeTest() {
        User testUser = new User(1L, "test@ya-test.ru", "bad_comedian",
                null, LocalDate.of(1991, 5, 24), new HashSet<>());
        final User user = userService.createUser(testUser);

        filmService.addLike(testFilm.getId(), user.getId());
        Assertions.assertEquals(testFilm.getLikes().size(), 1);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.addLike(null, user.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.addLike(testFilm.getId(), null));

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.addLike(777L, user.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.addLike(testFilm.getId(), 777L));
    }

    @Test
    public void removeLikeTest() {
        User testUser = new User(1L, "test@ya-test.ru", "bad_comedian",
                null, LocalDate.of(1991, 5, 24), new HashSet<>());
        final User user = userService.createUser(testUser);

        filmService.addLike(testFilm.getId(), user.getId());
        Assertions.assertEquals(testFilm.getLikes().size(), 1);

        filmService.removeLike(testFilm.getId(), user.getId());
        Assertions.assertEquals(testFilm.getLikes().size(), 0);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.removeLike(null, user.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.removeLike(testFilm.getId(), null));

        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.removeLike(777L, user.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> filmService.removeLike(testFilm.getId(), 777L));
    }

    @Test
    public void getPopularFilmsTest() {
        User testUser = new User(1L, "test@ya-test.ru", "bad_comedian",
                null, LocalDate.of(1991, 5, 24), new HashSet<>());
        final User user = userService.createUser(testUser);

        filmService.addLike(testFilm.getId(), user.getId());
        final List<Film> popFilmList = filmService.getPopularFilms(10);
        Assertions.assertNotNull(popFilmList);
        Assertions.assertEquals(popFilmList.size(), 1);

        Assertions.assertThrows(ValidationException.class,
                () -> filmService.getPopularFilms(0));

        Assertions.assertThrows(ValidationException.class,
                () -> filmService.getPopularFilms(-1));
    }

    private Film createFilm() {
        return new Film(37L, "Back to the Future Part III",
                "Third part of the legendary movie",
                LocalDate.of(1990, 5, 25),
                118L, null, new HashSet<>(), new HashSet<>());
    }

}
