package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

	private Film newFilm;
	private User newUser;
	private FilmController filmController;
	private UserController userController;

	@BeforeEach
	void setUp() {
		newFilm = Film.builder()
				.id(1)
				.name("Back to the Future Part III")
				.description("Third part of the legendary movie")
				.releaseDate(LocalDate.of(1990,5,25))
				.duration(118)
				.build();

		newUser = User.builder()
				.id(1)
				.email("test@ya-test.ru")
				.login("bad_comedian")
				.birthday(LocalDate.of(1991,5,24))
				.build();

		filmController = new FilmController();
		userController = new UserController();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void validateFilm() {
		filmController.validateFilm(newFilm);

		newFilm.setReleaseDate(LocalDate.of(1885,9,2));
		Assertions.assertThrows(ValidationException.class,
				() -> filmController.validateFilm(newFilm));
	}

	@Test
	void validateUser() {
		userController.validateUser(newUser);
		Assertions.assertEquals(newUser.getName(), newUser.getLogin());

		newUser.setName("Евген");
		userController.validateUser(newUser);
		Assertions.assertEquals("Евген", newUser.getName());
	}

}

