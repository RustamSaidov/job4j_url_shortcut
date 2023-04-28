package ru.job4j.url_shortcut.service;

import ru.job4j.url_shortcut.model.UrlEntity;
import ru.job4j.url_shortcut.model.UrlEntityDTO;

import java.util.List;
import java.util.Optional;

public interface UrlEntityService {

    Optional<UrlEntity> save(UrlEntity urlEntity);

    boolean deleteById(int id);

    Optional<UrlEntity> findById(int id);

    List<UrlEntity> findAll();

    Optional<UrlEntity> findByUrlLine(String urlLine);

    Optional<UrlEntity> findByConvertedUrl(String convertedUrl);

    Optional<UrlEntity> increaseRequestStat(UrlEntity urlEntity);

    List<UrlEntityDTO> getStatByUrlsForSite(String login);
}
