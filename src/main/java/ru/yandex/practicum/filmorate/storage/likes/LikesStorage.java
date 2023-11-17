package ru.yandex.practicum.filmorate.storage.likes;

import java.util.List;

public interface LikesStorage {

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    Integer getLikesCount(Long filmId);

}
