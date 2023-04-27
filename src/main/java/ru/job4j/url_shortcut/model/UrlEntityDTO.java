package ru.job4j.url_shortcut.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlEntityDTO {
    private String urlLine;
    private int totalCount;
}
