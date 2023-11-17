package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class MpaRating {

    @Min(1)
    private int id;

    @NotBlank
    private String name;

}
