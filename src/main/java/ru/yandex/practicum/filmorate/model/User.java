package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private long id;

    @NotEmpty
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

}
