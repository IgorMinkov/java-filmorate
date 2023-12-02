package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAll() {
        log.info("Получен запрос на получение всех режиссеров");
        return directorService.getAll();
    }

    @GetMapping("/{directorId}")
    public Director getById(@PathVariable @Positive Long directorId) {
        log.info("Получен запрос на получение режиссера с id: {}", directorId);
        return directorService.getById(directorId);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Получен директор для создания: {}", director);
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Получен директор для обновления: {}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable @Positive Long directorId) {
        log.info("Получен запрос на удаление директора с id: {}", directorId);
        directorService.deleteDirector(directorId);
    }
}
