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
import ru.job4j.url_shortcut.model.Person;
import ru.job4j.url_shortcut.model.PersonCredentials;
import ru.job4j.url_shortcut.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final ObjectMapper objectMapper;

    public PersonController(PersonService persons, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.persons = persons;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    /*GET с использованием ResponseEntity:*/
    @GetMapping("/all")
    public ResponseEntity<List<Person>> example2() {
        return ResponseEntity.of(Optional.of(persons.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<Person>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/registration")
    public void registration(@Valid @RequestBody Person person) throws MalformedURLException {
//        try {
//            UrlValidator.getInstance().isValid(person.getSite());
//        } catch (MalformedURLException exception){
//
//        }

        if(!UrlValidator.getInstance().isValid(person.getSite())){
            throw new MalformedURLException("This URL is not correct");
        }

        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }

        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }

        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        try {
            this.persons.save(person);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            this.persons.deleteById(id);
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
    public ResponseEntity<Person> findByUsername(@RequestParam String login) {
        var person = persons.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account is not found. Please, check requisites!!!!!!!!!"
                ));
        return ResponseEntity.of(Optional.of(person));
    }
/*
    @GetMapping
    public Person findByUsername(@RequestParam String login) {
        return persons.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check requisites!!!!!!!!!"
                ));
    }
 */

    /*Пример использования PATCH метода для частичного обновления данных:*/
    @PatchMapping("/change-user")
    public boolean changePersonUsingPatchMethod(@Valid @RequestBody PersonCredentials personDTO) throws Exception {
        personDTO.setPassword(encoder.encode(personDTO.getPassword()));
        boolean result = persons.update(personDTO);
        if (!result) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Person with this credentials is not found. Please, check requisites."
            );
        }
        return result;
    }
}
