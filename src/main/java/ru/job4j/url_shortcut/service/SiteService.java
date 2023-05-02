package ru.job4j.url_shortcut.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.modelDTO.SiteCredentials;
import ru.job4j.url_shortcut.modelDTO.SiteLine;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

public interface SiteService {

    String convertURL(String site);

    Site fillSiteByCredentials(String site);

    Optional<SiteCredentials> registration(SiteLine siteLine) throws MalformedURLException;

    Optional<Site> save(Site site);

    boolean deleteById(int id);

    boolean update(SiteCredentials siteDTO);

    Optional<Site> findById(int id);

    List<Site> findAll();

    UserDetails loadUserByUsername(String username);

    Optional<Site> findByLogin(String login);
}