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
import ru.job4j.url_shortcut.model.UrlEntity;
import ru.job4j.url_shortcut.model.UrlEntityDTO;
import ru.job4j.url_shortcut.repository.SiteRepository;
import ru.job4j.url_shortcut.repository.UrlEntityRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@ThreadSafe
@Service
@AllArgsConstructor
public class SimpleUrlEntityService implements UrlEntityService {

    private final UrlEntityRepository urlEntityRepository;
    private final SiteRepository siteRepository;


    @Override
    public Optional<UrlEntity> save(UrlEntity urlEntity) {
        return Optional.of(urlEntityRepository.save(urlEntity));
    }

    @Override
    public boolean deleteById(int id) {
        return urlEntityRepository.deleteById(id);
    }

    @Override
    public Optional<UrlEntity> findById(int id) {
        return urlEntityRepository.findById(id);
    }

    @Override
    public List<UrlEntity> findAll() {
        return urlEntityRepository.findAll();
    }

    @Override
    public Optional<UrlEntity> findByUrlLine(String urlLine) {
        return urlEntityRepository.findByUrlLine(urlLine);
    }

    @Override
    public Optional<UrlEntity> findByConvertedUrl(String convertedUrl) {
        return urlEntityRepository.findByConvertedUrl(convertedUrl);
    }

    @Override
    public Optional<UrlEntity> increaseRequestStat(UrlEntity urlEntity) {
        urlEntity.setTotalCount(urlEntity.getTotalCount()+1);
        return Optional.of(urlEntityRepository.save(urlEntity));
    }

    @Override
    public List<UrlEntityDTO> getStatByUrlsForSite(String login) {
        Site site = siteRepository.findByLogin(login).get();
        urlEntityRepository.findAllBySite(site);
        return null;
    }

//    @Override
//    public boolean update(SiteCredentials siteDTO) {
//        boolean result = false;
//        var current = siteRepository.findByLogin(siteDTO.getLogin());
//        if (current.isEmpty()) {
//            return result;
//        }
//        siteRepository.save(new Site(current.get().getId(), current.get().getSiteLine(), siteDTO.getLogin(), siteDTO.getPassword()));
//        result = true;
//        return result;
//    }

//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        var site = siteRepository.findByLogin(username);
//        if (site.isEmpty()) {
//            throw new UsernameNotFoundException(username);
//        }
//        return new User(site.get().getLogin(), site.get().getPassword(), emptyList());
//    }

}
