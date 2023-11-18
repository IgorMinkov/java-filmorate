package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void userCrudTest() {
        User newUser = makeUser();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        User savedUser = userStorage.getUserById(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);

        List<User> users = userStorage.getAllUsers();
        Assertions.assertNotNull(users);

        User updatedUser = userStorage.updateUser(savedUser);

        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }

    private User makeUser() {
        return new User(null, "test@ya-test.ru", "bad_comedian",
                null, LocalDate.of(1991, 5, 24), null);
    }

}