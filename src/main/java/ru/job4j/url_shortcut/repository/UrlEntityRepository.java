package ru.job4j.url_shortcut.repository;

import org.springframework.data.repository.CrudRepository;
import org.sql2o.Sql2o;
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

    default Optional<UrlEntity> incrementTotal(int id, Sql2o sql2o) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("UPDATE url_entity SET total = total + 1 WHERE id = :id");
            query.addParameter("id", id);
            var affectedRows = query.executeUpdate().getResult();
            if (affectedRows == 0) {
                return Optional.empty();
            }
            UrlEntity updatedEntity = findById(id).orElse(null);
            return Optional.ofNullable(updatedEntity);
        }
    }
}
