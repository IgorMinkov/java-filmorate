package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Friendship {

    @NotNull
    private int userId;

    @NotNull
    private int friendId;

    private boolean approved = false;

}
