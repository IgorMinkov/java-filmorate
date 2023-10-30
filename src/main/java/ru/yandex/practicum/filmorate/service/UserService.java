package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        if (user == null) {
            throw new ValidationException("в метод передан null");
        }
        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (user == null || !getAllUsers().contains(user)) {
            throw new DataNotFoundException(
                    String.format("Не найден пользователь для обновления: %s", user));
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id).orElseThrow(() ->
                new DataNotFoundException(
                        String.format("Не найден пользователь c id: %s", id)));
    }

    public void addFriend(Long id, Long friendId) {
        getUserById(id).getFriends().add(getUserById(friendId).getId());
        getUserById(friendId).getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        getUserById(id).getFriends().remove(getUserById(friendId).getId());
        getUserById(friendId).getFriends().remove(id);
    }

    public List<User> getUserFriendList(Long id) {
        User user = getUserById(id);
        List<User> friendList = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friendList.add(getUserById(friendId));
        }
        return friendList;
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        return getUserFriendList(id).stream()
                .filter(x -> getUserFriendList(otherId).contains(x))
                .collect(Collectors.toList());
    }

    public void validateUser(User user) {
        if (user.getName() == null || StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }

}
