package ru.job4j.url_shortcut.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteLine {
    @NotBlank(message = "Site url must be not empty")
    private String site;
}
