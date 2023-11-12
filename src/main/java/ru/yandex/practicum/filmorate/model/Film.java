package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Min(1)
    private long duration;

    private MpaRating mpaRating;

    private Set<Long> likes = new HashSet<>();

    private final List<Genre> genres = new ArrayList<>();

    public long getLikesCount() {
        return likes.size();
    }

}

