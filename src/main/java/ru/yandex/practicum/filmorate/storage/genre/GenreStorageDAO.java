package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreStorageDAO implements GenreStorage{ // нужен ли метод-геттер всех для фильма?

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres GROUP BY id";
        return jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre);
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genres g WHERE g.id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, id);
        if (genres.size() != 1) {
            throw new DataNotFoundException(String.format("нашлось больше одного жанра по id %s", id));
        }
        return Optional.of(genres.get(0));
    }

    @Override
    public Set<Genre> getGenresOfFilm(Long filmId) {
        String sqlQuery = "SELECT fg.genre_id, g.genre FROM film_genres AS fg " +
                "LEFT JOIN GENRES g on g.id = fg.genre_id WHERE fg.film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, filmId));
    }

    @Override
    public void updateGenresOfFilm(Film film) {
        String sqlClearBefore = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlClearBefore, film.getId());

        if (!film.getGenres().isEmpty()) {
            for (Integer genreId : film.getGenres()) {
                String sqlQuery = "INSERT INTO FILM_GENRES SET FILM_ID = ?, GENRE_ID = ?";
                jdbcTemplate.update(sqlQuery, film.getId(), genreId);
            }
        }
    }

    static Genre buildGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("genre"))
                .build();
    }

}
