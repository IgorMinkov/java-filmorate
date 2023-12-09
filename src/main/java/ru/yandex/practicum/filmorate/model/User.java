package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    @NotEmpty
    @Email
    @Size(max = 127)
    private String email;

    @NotBlank
    @Size(max = 127)
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$")
    private String login;

    @Size(max = 127)
    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

}
