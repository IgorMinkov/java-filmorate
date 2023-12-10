package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class MpaRating {

    @Min(1)
    private Integer id;

    @NotBlank
    @Size(max = 63)
    private String name;

}
