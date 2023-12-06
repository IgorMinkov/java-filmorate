package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Event> getUserFeed(Long id) {
        String sqlOperation = "SELECT * FROM events WHERE user_id = ?";
        List<Event> events = jdbcTemplate.query(sqlOperation, EventDbStorage::buildEvent, id);
        String result = events.stream()
                .map(Event::toString)
                .collect(Collectors.joining(", "));
        log.info("Список событий по запросу: {}", result);
        return events;
    }

    @Override
    public Event addEvent(Long userId, Long entityId, String eventType, String operationType) {

        SimpleJdbcInsert eventInsertion = new SimpleJdbcInsert(jdbcTemplate).withTableName("events")
                .usingGeneratedKeyColumns("event_id");

        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(Event.EventType.fromName(eventType))
                .operation(Event.OperationType.fromName(operationType))
                .timestamp(System.currentTimeMillis())
                .build();
        Long eventId = eventInsertion.executeAndReturnKey(event.toMap()).longValue();

        Event newEvent = getEvent(eventId);
        log.info("Создано событие: {} ", newEvent);

        return newEvent;
    }

    @Override
    public Event getEvent(Long id) {
        checkEventId(id);
        String sqlOperation = "SELECT * FROM events WHERE event_id = ?";
        Event event = jdbcTemplate.queryForObject(sqlOperation, EventDbStorage::buildEvent, id);
        log.info("Найдено событие: {} ", id);
        return event;
    }

    public void checkEventId(Long eventId) {
        String sqlOperation = "SELECT event_id FROM events WHERE event_id = ?";
        SqlRowSet sqlId = jdbcTemplate.queryForRowSet(sqlOperation, eventId);
        if (!sqlId.next()) {
            log.info("Событие с идентификатором {} не найдено.", eventId);
            throw new DataNotFoundException(String.format("Событие с id: %d не найдено", eventId));
        }
    }

    public static Event buildEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(Event.EventType.fromName(rs.getString("event_type")))
                .operation(Event.OperationType.fromName(rs.getString("operation_type")))
                .timestamp(rs.getLong("time_stamp"))
                .build();
    }
}