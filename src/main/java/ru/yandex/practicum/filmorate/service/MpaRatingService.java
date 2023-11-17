package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingService {

    private final MpaRatingStorage mpaRatingStorage;

    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(Long id) {
        return mpaRatingStorage.getMpaRatingById(id).orElseThrow(() ->
                new DataNotFoundException(
                        String.format("Не найден рейтинг c id: %s", id)));
    }

}
