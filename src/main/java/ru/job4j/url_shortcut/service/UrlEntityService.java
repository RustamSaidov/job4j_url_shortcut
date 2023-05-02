package ru.job4j.url_shortcut.service;

import org.springframework.web.bind.annotation.RequestBody;
import ru.job4j.url_shortcut.model.UrlEntity;
import ru.job4j.url_shortcut.modelDTO.Code;
import ru.job4j.url_shortcut.modelDTO.Url;
import ru.job4j.url_shortcut.modelDTO.UrlEntityDTO;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

public interface UrlEntityService {

    Optional<Code> convert(@Valid @RequestBody Url url) throws MalformedURLException;

    Optional<Url> redirect(String code);

    Optional<UrlEntity> save(UrlEntity urlEntity);

    boolean deleteById(int id);

    Optional<UrlEntity> findById(int id);

    List<UrlEntity> findAll();

    Optional<UrlEntity> findByUrlLine(String urlLine);

    Optional<UrlEntity> findByConvertedUrl(String convertedUrl);

    Optional<UrlEntity> increaseRequestStat(UrlEntity urlEntity);

    List<UrlEntityDTO> getStatByUrlsForSite(String login);
}
