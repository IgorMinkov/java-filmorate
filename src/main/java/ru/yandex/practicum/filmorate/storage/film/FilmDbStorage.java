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
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreStorage genreStorage;

    private final MpaRatingStorage mpaStorage;

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery =  "SELECT f.*, m.rating_name FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating = m.id";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            return statement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        mpaStorage.checkMpaRating(film.getMpaRating().getId());
        String sql = "UPDATE films SET mpa_rating = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getMpaRating().getId(), film.getId());

        genreStorage.updateFilmGenres(film);
        checkFilm(film.getId());
        log.info("Создан фильм: {}", film);
        return film;
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
                film.getMpaRating().getId(),
                film.getId());
        genreStorage.updateFilmGenres(film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT f.*, m.rating_name FROM films f" +
                " LEFT JOIN mpa_rating m ON f.mpa_rating = m.id WHERE f.film_id = ?";
        Film film = jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, id).stream()
                .findAny().orElseThrow(() -> new DataNotFoundException("не найден фильм с id" + id));
        film.setGenres(genreStorage.getFilmGenres(id));
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration," +
                " f.mpa_rating, m.rating_name" +
                " FROM films f" +
                " LEFT OUTER JOIN likes l ON l.film_id = f.film_id" +
                " LEFT JOIN mpa_rating m ON f.mpa_rating = m.id" +
                " GROUP BY f.film_id ORDER BY COUNT(l.user_id), f.film_id DESC LIMIT (?)";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, limit).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void checkFilm(Long id) {
        try {
            Film film = getFilmById(id);
            if (film == null) {
                throw new DataNotFoundException(String.format("не найден фильм с id %s", id));
            }
            log.trace("check film id: {} - OK", id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("в БД не найден фильм с id %s", id));
        }
    }

    static Film buildFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpaRating(MpaRating.builder()
                        .id(rs.getInt("mpa_rating"))
                        .name(rs.getString("rating_name"))
                        .build())
                .build();
    }

}