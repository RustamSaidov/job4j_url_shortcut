package ru.job4j.url_shortcut.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlEntityDTO {
    private String url;
    private int total;
}
