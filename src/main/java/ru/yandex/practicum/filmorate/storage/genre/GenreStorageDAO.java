package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreStorageDAO implements GenreStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres GROUP BY id";
        return jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        checkGenre(id);
        String sqlQuery = "SELECT * FROM genres g WHERE g.id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, id);
        return genres.get(0);
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        String sqlQuery = "SELECT fg.genre_id, g.genre FROM film_genres AS fg " +
                "LEFT JOIN GENRES g on g.id = fg.genre_id WHERE fg.film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, filmId));
    }

    @Override
    public void updateFilmGenres(Film film) {
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "MERGE INTO film_genres (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void checkGenre(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM film_genres WHERE film_id = ?";
            List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, id);
            if (genres.isEmpty()) {
                throw new DataNotFoundException(String.format("не найден жанр с id %s", id));
            }
            if (genres.size() != 1) {
                throw new DataNotFoundException(String.format("нашлось больше одного жанра с id %s", id));
            }
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("Ошибка SQL- в БД нет жанра с id %s", id));
        }
    }

    static Genre buildGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("genre"))
                .build();
    }

}
