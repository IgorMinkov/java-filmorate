package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
public class UserServiceTests {

    private User newUser;
    private User testUser;

    private final UserService userService;

    @Autowired
    public UserServiceTests(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        newUser = createUser();
        testUser = userService.createUser(newUser);
    }

    @Test
    public void createAndGetUsersTest() {
        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(newUser,testUser);

        List<User> usersList = userService.getAllUsers();
        Assertions.assertNotNull(usersList);
    }

    @Test
    public void updateUserTest() {
        User updatedUser = userService.updateUser(testUser);
        Assertions.assertEquals(testUser,updatedUser);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.updateUser(null));

        User unknownUser = new User(999, "hello@ya.ru", "newbie",
                null, LocalDate.of(2007,1,1), new HashSet<>());

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.updateUser(unknownUser));
    }

    @Test
    public void getUserByIdTest() {
        final User user = userService.getUserById(testUser.getId());
        Assertions.assertNotNull(user);
        Assertions.assertEquals(testUser,user);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.getUserById(null));

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.getUserById(999L));
    }

    @Test
    public void addFriendTest() {
        User inputUser =  new User(10, "test@yahoo.com", "critic",
                null, LocalDate.of(1985,8,19), new HashSet<>());
        User secondUser = userService.createUser(inputUser);

        Assertions.assertEquals(testUser.getFriends().size(),0);
        Assertions.assertEquals(secondUser.getFriends().size(),0);

        userService.addFriend(testUser.getId(), secondUser.getId());
        Assertions.assertEquals(testUser.getFriends().size(),1);
        Assertions.assertEquals(secondUser.getFriends().size(),1);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.addFriend(null, secondUser.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.addFriend(testUser.getId(), null));

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.addFriend(999L, secondUser.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.addFriend(testUser.getId(), 999L));
    }

    @Test
    public void deleteFriendTest() {
        User inputUser =  new User(10, "test@yahoo.com", "critic",
                null, LocalDate.of(1985,8,19), new HashSet<>());
        User secondUser = userService.createUser(inputUser);

        userService.addFriend(testUser.getId(), secondUser.getId());
        Assertions.assertEquals(testUser.getFriends().size(),1);
        Assertions.assertEquals(secondUser.getFriends().size(),1);

        userService.deleteFriend(testUser.getId(), secondUser.getId());
        Assertions.assertEquals(testUser.getFriends().size(),0);
        Assertions.assertEquals(secondUser.getFriends().size(),0);

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.deleteFriend(null, secondUser.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.deleteFriend(testUser.getId(), null));

        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.deleteFriend(999L, secondUser.getId()));
        Assertions.assertThrows(DataNotFoundException.class,
                () -> userService.deleteFriend(testUser.getId(), 999L));

    }

    @Test
    public void getUserFriendListTest() {
        User inputUser =  new User(10, "test@yahoo.com", "critic",
                null, LocalDate.of(1985,8,19), new HashSet<>());
        User secondUser = userService.createUser(inputUser);

        userService.addFriend(testUser.getId(), secondUser.getId());
        final List<User> friendList = userService.getUserFriendList(testUser.getId());
        Assertions.assertNotNull(friendList);
        Assertions.assertTrue(friendList.contains(secondUser));
        Assertions.assertTrue(userService.getUserFriendList(secondUser.getId())
                .contains(testUser));
    }

    @Test
    public void findCommonFriendsTest() {
        User inputUser =  new User(10, "test@yahoo.com", "critic",
                null, LocalDate.of(1985,8,19), new HashSet<>());
        User secondUser = userService.createUser(inputUser);

        User inputOneMore = new User(20, "test@test.kz", "hello",
                null, LocalDate.of(1988,8,11), new HashSet<>());
        User thirdUser = userService.createUser(inputOneMore);

        userService.addFriend(testUser.getId(), secondUser.getId());
        userService.addFriend(testUser.getId(), thirdUser.getId());

        final List<User> commonFriendList = userService
                .findCommonFriends(secondUser.getId(), thirdUser.getId());

        Assertions.assertNotNull(commonFriendList);
        Assertions.assertTrue(commonFriendList.contains(testUser));
    }

    @Test
    void validateUser() {
        userService.validateUser(newUser);
        Assertions.assertEquals(newUser.getName(), newUser.getLogin());

        newUser.setName("Евген");
        userService.validateUser(newUser);
        Assertions.assertEquals("Евген", newUser.getName());
    }

    private User createUser() {
        return new User(1, "test@ya-test.ru", "bad_comedian",
                null, LocalDate.of(1991,5,24), new HashSet<>());
    }

}
