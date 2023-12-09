package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService, EventService eventService) {
        this.userService = userService;
        this.filmService = filmService;
        this.eventService = eventService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@Positive @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен пользователь для создания: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен пользователь для обновления: {}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable("userId") Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        userService.delete(userId);
        log.info("Отправлен ответ на DELETE-запрос /users/{}", userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Для пользователя с id: {} добавляется друг с id : {}", id, friendId);
        userService.addFriend(id, friendId);
        eventService.addEvent(id, friendId, "FRIEND", "ADD");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Для пользователя с id: {} удаляется друг с id : {}", id, friendId);
        userService.deleteFriend(id, friendId);
        eventService.addEvent(id, friendId, "FRIEND", "REMOVE");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@Positive @PathVariable Long id) {
        return userService.getFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@Positive @PathVariable Long id, @Positive @PathVariable Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@Positive @PathVariable("id") Long userId) {
        log.info("Запрошены рекомендации для пользователя с id: {}", userId);
        return filmService.getRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@RequestBody @PathVariable("id") @Min(0) Long id) {
        log.info("Получен GET-запрос users/{id}/feed с id {} ", id);
        return eventService.getUserFeed(id);
    }
}
