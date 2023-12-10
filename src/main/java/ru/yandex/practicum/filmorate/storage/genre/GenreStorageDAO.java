package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Component
@RequiredArgsConstructor
public class GenreStorageDAO implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genres GROUP BY id";
        return jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre);
    }

    @Override
    public Genre getById(Integer id) {
        String sqlQuery = "SELECT * FROM genres g WHERE g.id = ?";
        return jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, id).stream()
                .findAny().orElseThrow(() -> new DataNotFoundException("не найден жанр с id" + id));
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        String sqlQuery = "SELECT fg.genre_id id, g.genre FROM film_genres fg " +
                "LEFT JOIN genres g ON g.id = fg.genre_id WHERE fg.film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, GenreStorageDAO::buildGenre, filmId));
    }

    @Override
    public void updateFilmGenres(Film film) {
        String deleteQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteQuery, film.getId());

        if (Objects.nonNull(film.getGenres()) && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "iNSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void fetchFilmGenres(List<Film> films) {
        Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        Set<Long> ids = filmById.keySet();

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sqlQuery = String.format("SELECT g.genre, g.id, fg.film_id film_id FROM film_genres fg " +
                "LEFT JOIN genres g ON g.id = fg.genre_id WHERE fg.film_id IN (%s)", inSql);

        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmById.get(filmId);
            film.addGenre(buildGenre(rs, rowNum));

            return null;
        }, ids.toArray());
    }

    private static Genre buildGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("genre"))
                .build();
    }

}
