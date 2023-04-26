package ru.job4j.url_shortcut.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.model.SiteCredentials;
import ru.job4j.url_shortcut.model.UrlEntity;

import java.util.List;
import java.util.Optional;

public interface UrlEntityService {

    Optional<UrlEntity> save(UrlEntity urlEntity);

    boolean deleteById(int id);

//    boolean update(SiteCredentials siteDTO);

    Optional<UrlEntity> findById(int id);

    List<UrlEntity> findAll();

//    UserDetails loadUserByUsername(String username);

    Optional<UrlEntity> findByUrlLine(String urlLine);
}
