package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorageDAO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorageDAO genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenreById(Integer id) {
        return genreStorage.getById(id);
    }

}
