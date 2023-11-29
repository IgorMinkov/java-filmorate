package ru.yandex.practicum.filmorate.storage.friends;

import java.util.Set;

public interface FriendshipStorage {

    void addFriend(Long userId, Long friendId);

    Set<Long> getFriends(Long userId);

    void deleteFriend(Long userId, Long friendId);

}
