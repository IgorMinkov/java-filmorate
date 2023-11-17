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
    public List<MpaRating> getAllMpaRatings() {
        String sqlQuery = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sqlQuery, MpaRatingDAO::buildMpaRating);
    }

    @Override
    public MpaRating getMpaRatingById(Integer id) {
        checkMpaRating(id);
        String sqlQuery = "SELECT * FROM mpa_rating mpa WHERE mpa.id = ?";
        List<MpaRating> ratings = jdbcTemplate.query(sqlQuery, MpaRatingDAO::buildMpaRating, id);
        return ratings.get(0);
    }

    @Override
    public void checkMpaRating(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM mpa_rating mpa WHERE mpa.id = ?";
            List<MpaRating> ratings = jdbcTemplate.query(sqlQuery, MpaRatingDAO::buildMpaRating, id);
            if (ratings.isEmpty()) {
                throw new DataNotFoundException(String.format("не найден рейтинг с id %s", id));
            }
            if (ratings.size() != 1) {
                throw new DataNotFoundException(String.format("нашлось больше одного рейтинга с id %s", id));
            }
        } catch (
                EmptyResultDataAccessException e) {
            throw new DataNotFoundException(String.format("Ошибка SQL - в БД нет рейтинга с id %s", id));
        }
    }

    static MpaRating buildMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("rating_name"))
                .build();
    }

}
