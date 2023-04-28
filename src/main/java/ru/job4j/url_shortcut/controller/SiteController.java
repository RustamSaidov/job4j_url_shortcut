package ru.job4j.url_shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.url_shortcut.model.*;
import ru.job4j.url_shortcut.service.SiteService;
import ru.job4j.url_shortcut.service.UrlEntityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/site")
public class SiteController {
    private final SiteService sites;
    private final UrlEntityService urlEntityService;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    public SiteController(SiteService sites, UrlEntityService urlEntityService, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.sites = sites;
        this.urlEntityService = urlEntityService;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    /*GET с использованием ResponseEntity:*/
    @GetMapping("/all")
    public ResponseEntity<List<Site>> getAll() {
        return ResponseEntity.of(Optional.of(sites.findAll()));
    }

    @GetMapping("/statistic")
    public ResponseEntity<List<UrlEntityDTO>> getStatByUrlsForSite() {
        String login = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.of(Optional.of(urlEntityService.getStatByUrlsForSite(login)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> findById(@PathVariable int id) {
        var site = this.sites.findById(id);
        return new ResponseEntity<Site>(
                site.orElse(new Site()),
                site.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Url> redirect(@PathVariable String code) {
        var urlEntity = this.urlEntityService.findByConvertedUrl(code);
        Optional<Url> url = Optional.empty();
        if (urlEntity.isPresent()) {
            url = Optional.of(new Url(urlEntity.get().getUrlLine()));
        }
        if (urlEntity.isPresent()) {
            this.urlEntityService.increaseRequestStat(urlEntity.get());
        }
        return new ResponseEntity<Url>(
                url.orElse(new Url()),
                url.isPresent() ? HttpStatus.FOUND : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/convert")
    public ResponseEntity<Code> convert(@Valid @RequestBody Url url) throws MalformedURLException {
        if (!UrlValidator.getInstance().isValid(url.getUrl())) {
            throw new MalformedURLException("This URL is not correct");
        }
        /*проверить, что адреса еще нет в БД. Если есть - вернуть имеющийся код*/
        Optional<UrlEntity> urlEntityInDB = urlEntityService.findByUrlLine(url.getUrl());
        if (urlEntityInDB.isPresent()) {
            return ResponseEntity.of(Optional.of(new Code(urlEntityInDB.get().getConvertedUrl())));
        }
        Code code = new Code(sites.convertURL(url.getUrl()));
        String login = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Site site = sites.findByLogin(login).get();
        UrlEntity urlEntity = new UrlEntity(0, url.getUrl(), code.getCode(), 0, site);
        urlEntityService.save(urlEntity);
        return ResponseEntity.of(Optional.of(code));
    }

    @PostMapping("/registration")
    public ResponseEntity<SiteCredentials> registration(@Valid @RequestBody SiteLine siteLine) throws MalformedURLException {
        if (!UrlValidator.getInstance().isValid(siteLine.getSite())) {
            throw new MalformedURLException("This URL is not correct");
        }
        var filledSite = sites.fillSiteByCredentials(siteLine.getSite());
        SiteCredentials siteDTO = new SiteCredentials(filledSite.getLogin(), filledSite.getPassword());
        filledSite.setPassword(encoder.encode(filledSite.getPassword()));
        var optionalSite = sites.save(filledSite);
        var site = optionalSite
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "SiteLine is not registered. May be its already exist in Database!!!!!!!!!"
                ));
        optionalSite.get().setPassword(siteDTO.getPassword());
        return ResponseEntity.of(Optional.of(siteDTO));
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Site site) {
        try {
            this.sites.save(site);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            this.sites.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        log.error(e.getLocalizedMessage());
    }

    /*GET с использованием ResponseEntity:*/
    @GetMapping
    public ResponseEntity<Site> findByUsername(@RequestParam String login) {
        var site = sites.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account is not found. Please, check requisites!!!!!!!!!"
                ));
        return ResponseEntity.of(Optional.of(site));
    }

    /*Пример использования PATCH метода для частичного обновления данных:*/
    @PatchMapping("/change-user")
    public boolean changeSiteUsingPatchMethod(@Valid @RequestBody SiteCredentials siteDTO) throws Exception {
        siteDTO.setPassword(encoder.encode(siteDTO.getPassword()));
        boolean result = sites.update(siteDTO);
        if (!result) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Site with this credentials is not found. Please, check requisites."
            );
        }
        return result;
    }
}
