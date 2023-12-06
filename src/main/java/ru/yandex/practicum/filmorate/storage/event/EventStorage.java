package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public interface EventStorage {

    List<Event> getUserFeed(Long id);

    Event getEvent(Long id);

    Event addEvent(Long userId, Long entityId, String eventType, String operationType);
}