package ru.job4j.urlshortcut.service;


import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.job4j.urlshortcut.model.Site;
import ru.job4j.urlshortcut.model.UrlEntity;
import ru.job4j.urlshortcut.dto.Code;
import ru.job4j.urlshortcut.dto.Url;
import ru.job4j.urlshortcut.dto.UrlEntityDTO;
import ru.job4j.urlshortcut.repository.SiteRepository;
import ru.job4j.urlshortcut.repository.UrlEntityRepository;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ThreadSafe
@Service
@AllArgsConstructor
public class SimpleUrlEntityService implements UrlEntityService {

    private final UrlEntityRepository urlEntityRepository;
    private final SiteRepository siteRepository;
    private final SiteService sites;

    @Override
    public Optional<Code> convert(@Valid @RequestBody Url url) throws MalformedURLException {
        if (!UrlValidator.getInstance().isValid(url.getUrl())) {
            throw new MalformedURLException("This URL is not correct");
        }
        /*проверить, что адреса еще нет в БД. Если есть - вернуть имеющийся код*/
        Optional<UrlEntity> urlEntityInDB = findByUrlLine(url.getUrl());
        if (urlEntityInDB.isPresent()) {
            return Optional.of(new Code(urlEntityInDB.get().getConvertedUrl()));
        }
        Code code = new Code(sites.convertURL(url.getUrl()));
        String login = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Site site = sites.findByLogin(login).get();
        UrlEntity urlEntity = new UrlEntity(0, url.getUrl(), code.getCode(), 0, site);
        save(urlEntity);
        return Optional.of(code);
    }

    @Override
    public Optional<Url> redirect(String code) {
        var urlEntity = findByConvertedUrl(code);
        Optional<Url> url = Optional.empty();
        if (urlEntity.isPresent()) {
            url = Optional.of(new Url(urlEntity.get().getUrlLine()));
        }
        if (urlEntity.isPresent()) {
            increaseRequestStat(urlEntity.get());
        }
        return url;
    }

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
    public synchronized Optional<UrlEntity> increaseRequestStat(UrlEntity urlEntity) {
        urlEntityRepository.incrementTotal(urlEntity.getId());
        return Optional.of(urlEntityRepository.save(urlEntity));
    }

    @Override
    public List<UrlEntityDTO> getStatByUrlsForSite(String login) {
        Site site = siteRepository.findByLogin(login).get();
        List<UrlEntity> urlEntityList = urlEntityRepository.findAllBySite(site);
        List<UrlEntityDTO> urlEntityDTOList = new ArrayList<>();
        urlEntityList.stream().forEach(u -> urlEntityDTOList.add(new UrlEntityDTO(u.getUrlLine(), u.getTotal())));
        return urlEntityDTOList;
    }


}
