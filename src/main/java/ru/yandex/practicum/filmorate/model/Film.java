package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Min(1)
    private Long duration;

    @JsonProperty("mpa")
    private MpaRating mpaRating;

    private Set<Genre> genres;

    private Set<Long> likes;

    public long getLikesCount() {
        if (likes == null) {
            return 0;
        } else {
            return likes.size();
        }
    }

}

