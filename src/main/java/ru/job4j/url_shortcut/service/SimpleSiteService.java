package ru.job4j.url_shortcut.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.modelDTO.SiteCredentials;
import ru.job4j.url_shortcut.modelDTO.SiteLine;
import ru.job4j.url_shortcut.repository.SiteRepository;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Slf4j
@ThreadSafe
@Service
@AllArgsConstructor
public class SimpleSiteService implements SiteService, UserDetailsService {

    private final BCryptPasswordEncoder encoder;
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
    public Optional<SiteCredentials> registration(SiteLine siteLine) throws MalformedURLException {
        if (!UrlValidator.getInstance().isValid(siteLine.getSite())) {
            throw new MalformedURLException("This URL is not correct");
        }
        var filledSite = fillSiteByCredentials(siteLine.getSite());
        SiteCredentials siteDTO = new SiteCredentials(filledSite.getLogin(), filledSite.getPassword());
        filledSite.setPassword(encoder.encode(filledSite.getPassword()));
        var optionalSite = save(filledSite);
        if (optionalSite.isPresent()) {
            return Optional.of(siteDTO);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Site> save(Site site) {
        Optional<Site> result = Optional.empty();
        try {
            result = Optional.of(siteRepository.save(site));
        } catch (Exception e) {
            log.error("An error occurred while SAVING site with siteLine {}", site.getSiteLine());
        }
        return result;
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