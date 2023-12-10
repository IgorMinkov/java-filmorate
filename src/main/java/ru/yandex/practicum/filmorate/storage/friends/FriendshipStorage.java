package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendshipStorage {

    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    void deleteFriend(Long userId, Long friendId);

}
