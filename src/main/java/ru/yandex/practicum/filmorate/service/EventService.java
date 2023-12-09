package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;



@Service
@RequiredArgsConstructor
public class EventService {

    private final EventStorage eventStorage;
    private final UserService userService;

    public List<Event> getUserFeed(Long id) {
        userService.validateUser(id);
        return eventStorage.getUserFeed(id);
    }

    public Event addEvent(Long userId, Long entityId, String eventType, String operationType) {
        userService.validateUser(userId);
        return eventStorage.addEvent(userId, entityId, eventType, operationType);
    }

}
