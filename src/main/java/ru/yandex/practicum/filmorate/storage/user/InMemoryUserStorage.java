package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userCounter = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        generateUserId(user);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public User getById(Long id) {
        checkUser(id);
        return users.get(id);
    }

    @Override
    public void checkUser(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new DataNotFoundException(
                    String.format("Не найден пользователь: %s", id));
        }
    }

    private void generateUserId(User user) {
        user.setId(++userCounter);
    }

}
