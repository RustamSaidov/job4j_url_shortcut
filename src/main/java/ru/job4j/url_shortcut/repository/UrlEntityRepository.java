package ru.job4j.url_shortcut.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.model.UrlEntity;

import java.util.List;
import java.util.Optional;

public interface UrlEntityRepository extends CrudRepository<UrlEntity, Integer> {
    List<UrlEntity> findAll();

    boolean deleteById(int id);

    Optional<UrlEntity> findByUrlLine(String urlLine);

    Optional<UrlEntity> findByConvertedUrl(String convertedUrl);

    List<UrlEntity> findAllBySite(Site site);

    @Transactional
    @Modifying
    @Query("UPDATE UrlEntity SET total = total + 1 WHERE id = :id")
    int incrementTotal(@Param("id") int id);
}
