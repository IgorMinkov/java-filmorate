package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage { // как добавлять жанры?

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM films GROUP BY film_id";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_rating) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setInt(5, film.getMpaRatingId());
            return statement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        Film createdFilm = null;

        Optional<Film> optFilm = getFilmById(film.getId());
        if(optFilm.isPresent()) {
            createdFilm = optFilm.get();
        }
        log.info("Создан фильм: {}", film);
        return createdFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilm(film.getId());
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_rating = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaRatingId(),
                film.getId());
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sqlQuery = "SELECT * FROM films f WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, id);
        if (films.size() != 1) {
            throw new DataNotFoundException(String.format("не найден фильм с id %s", id));
        }
        return Optional.of(films.get(0));
    }

    @Override
    public void checkFilm(Long id) {
        try {
            getFilmById(id);
            log.trace("check film id: {} - OK", id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("в БД не найден фильм с id %s", id));
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_rating" +
                " FROM films f LEFT OUTER JOIN likes l ON l.film_id = f.film_id " +
                "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT (?)";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, limit);
    }

    static Film buildFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpaRatingId(rs.getInt("mpa_rating"))
                .build();
    }

}
