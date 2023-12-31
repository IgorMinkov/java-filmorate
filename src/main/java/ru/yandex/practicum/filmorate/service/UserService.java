package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        if (user == null) {
            throw new ValidationException("в метод передан null");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        validateUser(id);
        return userStorage.getUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        validateUser(id);
        validateUser(friendId);
        friendshipStorage.addFriend(id,friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        validateUser(id);
        validateUser(friendId);
        friendshipStorage.deleteFriend(id,friendId);
    }

    public List<User> getUserFriendList(Long id) {
        validateUser(id);
        List<User> friendList = new ArrayList<>();
        for (Long friendId : friendshipStorage.getUserFriends(id)) {
            friendList.add(getUserById(friendId));
        }
        return friendList;
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        validateUser(id);
        validateUser(otherId);
        return getUserFriendList(id).stream()
                .filter(x -> getUserFriendList(otherId).contains(x))
                .collect(Collectors.toList());
    }

    protected void validateUser(Long id) {
        userStorage.checkUser(id);
    }

}
