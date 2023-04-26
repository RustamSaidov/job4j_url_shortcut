package ru.job4j.url_shortcut.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.model.UrlEntity;

import java.util.List;
import java.util.Optional;

public interface UrlEntityRepository extends CrudRepository<UrlEntity, Integer> {
    List<UrlEntity> findAll();

    boolean deleteById(int id);

    Optional<UrlEntity> findByUrlLine(String urlLine);
}
