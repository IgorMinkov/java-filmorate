package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> findCommonFriends(Long id, Long otherId);

    void deleteFriend(Long userId, Long friendId);

}
