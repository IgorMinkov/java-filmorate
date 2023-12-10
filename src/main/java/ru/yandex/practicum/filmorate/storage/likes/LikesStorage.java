package ru.yandex.practicum.filmorate.storage.likes;

import java.util.List;

public interface LikesStorage {

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    List<Long> getLikedFilmsId(Long userId);

    Long getSameLikesUserId(Long userId);

}
