package ru.job4j.urlshortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.urlshortcut.model.Site;
import ru.job4j.urlshortcut.dto.SiteCredentials;
import ru.job4j.urlshortcut.dto.SiteLine;
import ru.job4j.urlshortcut.service.SiteService;

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
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    public SiteController(SiteService sites, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.sites = sites;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    /*GET с использованием ResponseEntity:*/
    @GetMapping("/all")
    public ResponseEntity<List<Site>> getAll() {
        return ResponseEntity.of(Optional.of(sites.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> findById(@PathVariable int id) {
        var site = this.sites.findById(id);
        return new ResponseEntity<Site>(
                site.orElse(new Site()),
                site.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/registration")
    public ResponseEntity<SiteCredentials> registration(@Valid @RequestBody SiteLine siteLine) throws MalformedURLException {
        Optional<SiteCredentials> siteDTO = sites.registration(siteLine);
        var site = siteDTO
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "SiteLine is not registered. May be its already exist in Database!!!!!!!!!"
                ));
        return ResponseEntity.of(siteDTO);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Site site) {
        try {
            this.sites.save(site);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("An error occurred while updating site with id {}", site.getId(), exception);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            this.sites.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("An error occurred while deleting site with id {}", id, exception);
        }
        return ResponseEntity.notFound().build();
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
