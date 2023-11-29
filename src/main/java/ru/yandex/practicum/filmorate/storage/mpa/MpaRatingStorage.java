package ru.yandex.practicum.filmorate.storage.mpa;


import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingStorage {

    List<MpaRating> getAll();

    MpaRating getById(Integer id);

    void checkMpaRating(Integer id);

}
