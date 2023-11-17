package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
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

    private Integer mpaRatingId;

    private final Set<Integer> genres;

    private Set<Long> likes = new HashSet<>();

    public long getLikesCount() {
        return likes.size();
    }

}

