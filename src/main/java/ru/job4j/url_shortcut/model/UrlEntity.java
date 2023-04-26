package ru.job4j.url_shortcut.model;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class UrlEntity {

    private int id;
    private String urlLine;
    private String convertedUrl;
    private int totalCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Site site;
}
