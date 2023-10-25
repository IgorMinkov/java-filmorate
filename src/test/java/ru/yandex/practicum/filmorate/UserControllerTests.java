package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class UserControllerTests {

    private User newUser;
    private final UserController userController = new UserController();

    @BeforeEach
    void setUp() {
        newUser = createUser();
    }

    @Test
    public void createAndGetUsersTest() {
        User testUser = userController.createUser(newUser);
        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(newUser,testUser);

        List<User> usersList = userController.getAllUsers();
        Assertions.assertNotNull(usersList);
    }

    @Test
    public void updateUserTest() {
        User testUser = userController.createUser(newUser);
        User updatedUser = userController.updateUser(testUser);
        Assertions.assertEquals(testUser,updatedUser);

        Assertions.assertThrows(ValidationException.class,
                () -> userController.updateUser(null));

        User unknownUser = User.builder()
                .id(999)
                .email("hello@ya.ru")
                .login("newbie")
                .birthday(LocalDate.of(2007,1,1))
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> userController.updateUser(unknownUser));
    }

    @Test
    void validateUser() {
        userController.validateUser(newUser);
        Assertions.assertEquals(newUser.getName(), newUser.getLogin());

        newUser.setName("Евген");
        userController.validateUser(newUser);
        Assertions.assertEquals("Евген", newUser.getName());
    }

    private User createUser() {
        return User.builder()
                .id(1)
                .email("test@ya-test.ru")
                .login("bad_comedian")
                .birthday(LocalDate.of(1991,5,24))
                .build();
    }

}
