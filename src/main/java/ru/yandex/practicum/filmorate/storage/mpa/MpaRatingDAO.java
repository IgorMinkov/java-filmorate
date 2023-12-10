package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaRatingDAO implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAll() {
        String sqlQuery = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sqlQuery, MpaRatingDAO::buildMpaRating);
    }

    @Override
    public MpaRating getById(Integer id) {
        String sqlQuery = "SELECT * FROM mpa_rating mpa WHERE mpa.id = ?";
        return jdbcTemplate.query(sqlQuery, MpaRatingDAO::buildMpaRating, id).stream()
                .findAny().orElseThrow(() -> new DataNotFoundException("не найден рейтинг с id" + id));
    }

    @Override
    public void checkMpaRating(Integer id) {
        try {
            MpaRating rating = getById(id);
            if (rating == null) {
                throw new DataNotFoundException(String.format("не найден рейтинг с id %s", id));
            }
        } catch (
                EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("Ошибка SQL - в БД нет рейтинга с id %s", id));
        }
    }

    private static MpaRating buildMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("rating_name"))
                .build();
    }

}
