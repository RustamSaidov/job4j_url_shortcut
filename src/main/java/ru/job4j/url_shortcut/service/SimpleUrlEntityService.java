package ru.job4j.url_shortcut.service;


import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.model.UrlEntity;
import ru.job4j.url_shortcut.model.UrlEntityDTO;
import ru.job4j.url_shortcut.repository.SiteRepository;
import ru.job4j.url_shortcut.repository.UrlEntityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        urlEntity.setTotalCount(urlEntity.getTotalCount() + 1);
        return Optional.of(urlEntityRepository.save(urlEntity));
    }

    @Override
    public List<UrlEntityDTO> getStatByUrlsForSite(String login) {
        Site site = siteRepository.findByLogin(login).get();
        List<UrlEntity> urlEntityList = urlEntityRepository.findAllBySite(site);
        List<UrlEntityDTO> urlEntityDTOList = new ArrayList<>();
        urlEntityList.stream().forEach(u -> urlEntityDTOList.add(new UrlEntityDTO(u.getUrlLine(), u.getTotalCount())));
        return urlEntityDTOList;
    }


}
