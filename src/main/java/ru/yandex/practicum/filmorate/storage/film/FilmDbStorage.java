package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
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

    private final DirectorStorage directorStorage;


    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, m.rating_name FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating = m.id";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
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
        directorStorage.updateFilmDirectors(film);
        checkFilm(film.getId());
        log.info("Создан фильм: {}", film);
        return getById(keyHolder.getKey().longValue());
    }

    @Override
    public Film update(Film film) {
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
        directorStorage.updateFilmDirectors(film);
        log.info("Обновлен фильм: {}", film);
        return getById(film.getId());
    }

    @Override
    public void delete(Long filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, filmId);
        } catch (DataAccessException e) {
            throw new DataNotFoundException("Фильм не найден" + e.getMessage());
        }
    }

    @Override
    public Film getById(Long id) {
        String sqlQuery = "SELECT f.*, m.rating_name FROM films f" +
                " LEFT JOIN mpa_rating m ON f.mpa_rating = m.id WHERE f.film_id = ?";
        Film film = jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, id).stream()
                .findAny().orElseThrow(() -> new DataNotFoundException("не найден фильм с id" + id));
        film.setGenres(genreStorage.getFilmGenres(id));
        film.setDirectors(directorStorage.getByFilmId(id));
        return film;
    }

    @Override
    public List<Film> getPopular(Integer limit) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration," +
                " f.mpa_rating, m.rating_name" +
                " FROM films f" +
                " LEFT OUTER JOIN likes l ON l.film_id = f.film_id" +
                " LEFT JOIN mpa_rating m ON f.mpa_rating = m.id" +
                " GROUP BY f.film_id ORDER BY COUNT(l.user_id), f.film_id DESC LIMIT (?)";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, limit).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void checkFilm(Long id) {
        try {
            Film film = getById(id);
            if (film == null) {
                throw new DataNotFoundException(String.format("не найден фильм с id %s", id));
            }
            log.trace("check film id: {} - OK", id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("в БД не найден фильм с id %s", id));
        }
    }

    @Override
    public List<Film> getDirectorFilmsSortByYear(long directorId) {
        String sqlQuery = "SELECT f.*, mr.* FROM DIRECTORS d " +
                "JOIN FILM_DIRECTORS df ON d.ID = df.DIRECTOR_ID " +
                "JOIN FILMS f ON f.FILM_ID = df.FILM_ID " +
                "JOIN MPA_RATING mr ON mr.ID = f.MPA_RATING " +
                "WHERE d.ID = ?" +
                "ORDER BY f.RELEASE_DATE ASC";
        log.info("Получение фильмов режиссера с id {} с сортировкой по годам", directorId);
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, directorId).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getDirectorFilmsSortByLikes(long directorId) {
        String sqlQuery = "SELECT f.*, mr.* FROM DIRECTORS d " +
                "JOIN FILM_DIRECTORS df ON d.ID = df.DIRECTOR_ID " +
                "JOIN FILMS f ON df.FILM_ID = f.FILM_ID " +
                "LEFT OUTER JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                "JOIN MPA_RATING mr ON f.MPA_RATING = mr.ID " +
                "WHERE d.ID = ? " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.USER_ID) DESC";
        log.info("Получение фильмов режиссера с id {} с сортировкой по лайкам", directorId);
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, directorId).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())))
                .collect(Collectors.toList());
    }

    private static Film buildFilm(ResultSet rs, int rowNum) throws SQLException {
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
