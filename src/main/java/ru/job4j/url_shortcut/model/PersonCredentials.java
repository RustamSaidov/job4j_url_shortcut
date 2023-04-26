package ru.job4j.url_shortcut.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCredentials {
    @Size(min = 5, max = 50, message = "Login must be between 5 and 50 characters")
    private String login;
    @NotBlank(message = "Password must be not empty")
    private String password;

}
