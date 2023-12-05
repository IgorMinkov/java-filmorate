package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;



@Service
@RequiredArgsConstructor
public class EventService {

    @Qualifier("eventDbStorage")
    private final EventStorage eventStorage;
    private final UserService userService;

    public List<Event> getUserFeed(Long id) {
        userService.getUserById(id);
        return this.eventStorage.getUserFeed(id);
    }

    public Event addEvent(Long userId, Long entityId, String eventType, String operationType) {
        userService.getUserById(userId);
        return this.eventStorage.addEvent(userId, entityId, eventType, operationType);
    }
}