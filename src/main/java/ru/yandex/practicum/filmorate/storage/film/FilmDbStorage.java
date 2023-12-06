package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    public List<Film> getCommon(Long userId, Long friendId) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_rating, m.rating_name " +
                "FROM films f " +
                "LEFT OUTER JOIN likes ul ON f.film_id = ul.film_id " +
                "LEFT OUTER JOIN likes fl ON f.film_id = fl.film_id " +
                "LEFT JOIN mpa_rating m ON f.mpa_rating = m.id " +
                "WHERE ul.user_id = ? AND fl.user_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(ul.user_id), f.film_id DESC";

        log.debug("Получены общие фильмы у пользователей с id {} и {}.", userId, friendId);
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, userId, friendId).stream()
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

    @Override
    public List<Film> getSearchResults(String query, String[] paramsList) {
        List<Object> sqlArgs = new ArrayList<>();
        List<String> sqlConditions = new ArrayList<>();
        HashMap<String, String> searchConditions = new HashMap<>();
        searchConditions.put("director", "d.name iLike CONCAT('%',?,'%')");
        searchConditions.put("title", "f.name iLike CONCAT('%',?,'%')");
        for (String param : paramsList) {
            if (searchConditions.containsKey(param)) {
                sqlConditions.add(searchConditions.get(param));
                sqlArgs.add(query);
            }
        }
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration," +
                " f.mpa_rating, m.rating_name" +
                " from films f " +
                " LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                " LEFT JOIN directors d ON d.id = fd.director_id " +
                " LEFT JOIN mpa_rating m ON f.mpa_rating = m.id" +
                " LEFT JOIN film_genres g ON f.film_id = g.film_id" +
                " LEFT OUTER JOIN likes l ON l.film_id = f.film_id";
        if (!sqlConditions.isEmpty()) {
            sqlQuery += " WHERE " + String.join(" OR ", sqlConditions);
        }
        sqlQuery += " GROUP BY f.film_id ORDER BY COUNT(l.user_id), f.film_id DESC";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::buildFilm, sqlArgs.toArray()).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())))
                .collect(Collectors.toList());
      }

    public List<Film> getPopular(Long genreId, String year, Integer limit) {
        List<Object> sqlArgs = new ArrayList<>();
        List<String> sqlConditions = new ArrayList<>();
        if (year != null) {
            sqlConditions.add("EXTRACT(YEAR FROM f.release_date)=?");
            sqlArgs.add(year);
        }
        if (genreId != null) {
            sqlConditions.add("g.genre_id=?");
            sqlArgs.add(genreId);
        }
        String sqlQueryStatement = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration," +
                " f.mpa_rating, m.rating_name" +
                " FROM films f" +
                " LEFT OUTER JOIN likes l ON l.film_id = f.film_id" +
                " LEFT JOIN mpa_rating m ON f.mpa_rating = m.id" +
                " LEFT JOIN film_genres g ON f.film_id = g.film_id ";
        String sqlQueryGroupBy = " GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC, f.film_id DESC LIMIT (?)";
        String sqlCondition = String.join(" and ", sqlConditions);
        if (!sqlCondition.isEmpty()) {
            sqlCondition = "WHERE " + sqlCondition;
        }
        String resultQuery = sqlQueryStatement + sqlCondition + sqlQueryGroupBy;
        sqlArgs.add(limit);
        return jdbcTemplate.query(resultQuery, FilmDbStorage::buildFilm, sqlArgs.toArray()).stream()
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())))
                .collect(Collectors.toList());
    }
}
