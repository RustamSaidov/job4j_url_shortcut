package ru.job4j.url_shortcut.service;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.model.SiteCredentials;
import ru.job4j.url_shortcut.repository.SiteRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@ThreadSafe
@Service
@AllArgsConstructor
public class SimpleSiteService implements SiteService, UserDetailsService {

    private final SiteRepository siteRepository;
    private final int RANDOM_STRING_LENGTH = 20;
    private final int RANDOM_SHORT_URL_LINE = 7;

    @Override
    public String convertURL(String site) {
        String convertedURL = RandomStringUtils.random(RANDOM_SHORT_URL_LINE, true, true);
        return convertedURL;
    }

    @Override
    public Site fillSiteByCredentials(String site) {
        String generatedLogin = "";
        String generatedPassword = "";
        boolean flag = false;
        while (!flag) {
            generatedLogin = RandomStringUtils.random(RANDOM_STRING_LENGTH, true, true);
            generatedPassword = RandomStringUtils.random(RANDOM_STRING_LENGTH, true, true);
            if (siteRepository.findByLogin(generatedLogin).isEmpty() && siteRepository.findByPassword(generatedPassword).isEmpty()) {
                flag = true;
            }
        }
        return new Site(0, site, generatedLogin, generatedPassword);
    }

    @Override
    public Optional<Site> save(Site site) {
        return Optional.of(siteRepository.save(site));
    }

    @Override
    public boolean deleteById(int id) {
        return siteRepository.deleteById(id);
    }

    @Override
    public boolean update(SiteCredentials siteDTO) {
        boolean result = false;
        var current = siteRepository.findByLogin(siteDTO.getLogin());
        if (current.isEmpty()) {
            return result;
        }
        siteRepository.save(new Site(current.get().getId(), current.get().getSiteLine(), siteDTO.getLogin(), siteDTO.getPassword()));
        result = true;
        return result;
    }

    @Override
    public Optional<Site> findById(int id) {
        return siteRepository.findById(id);
    }

    @Override
    public List<Site> findAll() {
        return siteRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var site = siteRepository.findByLogin(username);
        if (site.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new User(site.get().getLogin(), site.get().getPassword(), emptyList());
    }

    @Override
    public Optional<Site> findByLogin(String login) {
        return siteRepository.findByLogin(login);
    }
}