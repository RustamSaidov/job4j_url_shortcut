package ru.job4j.url_shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.url_shortcut.modelDTO.Code;
import ru.job4j.url_shortcut.modelDTO.Url;
import ru.job4j.url_shortcut.modelDTO.UrlEntityDTO;
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
public class UrlEntityController {
    private final UrlEntityService urlEntityService;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    public UrlEntityController(UrlEntityService urlEntityService, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.urlEntityService = urlEntityService;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/statistic")
    public ResponseEntity<List<UrlEntityDTO>> getStatByUrlsForSite() {
        String login = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.of(Optional.of(urlEntityService.getStatByUrlsForSite(login)));
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Url> redirect(@PathVariable String code) {


        Optional<Url> url = urlEntityService.redirect(code);
        return new ResponseEntity<Url>(
                url.orElse(new Url()),
                url.isPresent() ? HttpStatus.FOUND : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/convert")
    public ResponseEntity<Code> convert(@Valid @RequestBody Url url) throws MalformedURLException {
        Optional<Code> code = urlEntityService.convert(url);
        return ResponseEntity.of(code);
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
}
