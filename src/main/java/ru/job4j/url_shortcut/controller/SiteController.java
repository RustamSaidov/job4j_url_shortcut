package ru.job4j.url_shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.url_shortcut.model.Code;
import ru.job4j.url_shortcut.model.Site;
import ru.job4j.url_shortcut.model.SiteCredentials;
import ru.job4j.url_shortcut.model.SiteLine;
import ru.job4j.url_shortcut.service.SiteService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/site")
public class SiteController {
    private final SiteService sites;
    private final BCryptPasswordEncoder encoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteController.class.getSimpleName());
    private final ObjectMapper objectMapper;

    public SiteController(SiteService sites, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.sites = sites;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    /*GET с использованием ResponseEntity:*/
    @GetMapping("/all")
    public ResponseEntity<List<Site>> example2() {
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

    @PostMapping("/convert")
    public ResponseEntity<Code> convert(@Valid @RequestBody SiteLine siteLine) throws MalformedURLException {
        if(!UrlValidator.getInstance().isValid(siteLine.getSite())){
            throw new MalformedURLException("This URL is not correct");
        }
        Code code = new Code(sites.convertURL(siteLine.getSite()));
        return ResponseEntity.of(Optional.of(code));
    }

    @PostMapping("/registration")
    public ResponseEntity<Site> registration(@Valid @RequestBody SiteLine siteLine) throws MalformedURLException {
        if(!UrlValidator.getInstance().isValid(siteLine.getSite())){
            throw new MalformedURLException("This URL is not correct");
        }
        var filledSite = sites.fillSiteByCredentials(siteLine.getSite());
        SiteCredentials siteDTO = new SiteCredentials(filledSite.getLogin(),filledSite.getPassword());
        filledSite.setPassword(encoder.encode(filledSite.getPassword()));
        var optionalSite = sites.save(filledSite);
        var site = optionalSite
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "SiteLine is not registered. May be its already exist in Database!!!!!!!!!"
                ));
        optionalSite.get().setPassword(siteDTO.getPassword());
        return ResponseEntity.of(optionalSite);
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody Site site) {
        if (site.getLogin() == null || site.getPassword() == null) {
            throw new NullPointerException("login and password mustn't be empty");
        }
        if (site.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }

        site.setPassword(encoder.encode(site.getPassword()));
        sites.save(site);
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
        LOGGER.error(e.getLocalizedMessage());
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
