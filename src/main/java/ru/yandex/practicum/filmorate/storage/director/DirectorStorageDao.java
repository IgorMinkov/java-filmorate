package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.*;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorStorageDao implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, DirectorStorageDao::buildDirector);
    }

    @Override
    public Set<Director> getByFilmId(Long filmId) {
        String sqlQuery = "SELECT d.* FROM film_directors df JOIN directors d ON df.director_id = d.id " +
                "WHERE df.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, DirectorStorageDao::buildDirector, filmId));
    }

    @Override
    public Director getById(Long directorId) {
        String sqlQuery = "SELECT * FROM directors WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, DirectorStorageDao::buildDirector, directorId).stream()
                .findAny()
                .orElseThrow(() -> new DataNotFoundException("Не найден режиссер с id " + directorId));
    }

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        return getById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public void updateFilmDirectors(Film film) {
        String sqlQuery = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (Objects.nonNull(film.getDirectors()) && !film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            jdbcTemplate.batchUpdate("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        if (rowsUpdated == 1) {
            return getById(director.getId());
        } else {
            throw new DataNotFoundException("Директор с id " + director.getId() + " не найден");
        }
    }

    @Override
    public void deleteDirector(Long directorId) {
        String sqlQuery = "DELETE FROM directors WHERE id = ? ";
        int rowsUpdated = jdbcTemplate.update(sqlQuery, directorId);
        if (rowsUpdated != 1) {
            throw new DataNotFoundException("Директор с id " + directorId + " не найден");
        }
    }

    private static Director buildDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
