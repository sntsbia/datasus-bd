package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Sintomas;
import com.datasus.bd.repository.SintomasRepository;
import com.datasus.bd.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.datasus.bd.domain.Sintomas}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SintomasResource {

    private final Logger log = LoggerFactory.getLogger(SintomasResource.class);

    private static final String ENTITY_NAME = "sintomas";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SintomasRepository sintomasRepository;

    public SintomasResource(SintomasRepository sintomasRepository) {
        this.sintomasRepository = sintomasRepository;
    }

    /**
     * {@code POST  /sintomas} : Create a new sintomas.
     *
     * @param sintomas the sintomas to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sintomas, or with status {@code 400 (Bad Request)} if the sintomas has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sintomas")
    public Mono<ResponseEntity<Sintomas>> createSintomas(@RequestBody Sintomas sintomas) throws URISyntaxException {
        log.debug("REST request to save Sintomas : {}", sintomas);
        if (sintomas.getId() != null) {
            throw new BadRequestAlertException("A new sintomas cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return sintomasRepository
            .save(sintomas)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/sintomas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /sintomas/:id} : Updates an existing sintomas.
     *
     * @param id the id of the sintomas to save.
     * @param sintomas the sintomas to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sintomas,
     * or with status {@code 400 (Bad Request)} if the sintomas is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sintomas couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sintomas/{id}")
    public Mono<ResponseEntity<Sintomas>> updateSintomas(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Sintomas sintomas
    ) throws URISyntaxException {
        log.debug("REST request to update Sintomas : {}, {}", id, sintomas);
        if (sintomas.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sintomas.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sintomasRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return sintomasRepository
                    .save(sintomas)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /sintomas/:id} : Partial updates given fields of an existing sintomas, field will ignore if it is null
     *
     * @param id the id of the sintomas to save.
     * @param sintomas the sintomas to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sintomas,
     * or with status {@code 400 (Bad Request)} if the sintomas is not valid,
     * or with status {@code 404 (Not Found)} if the sintomas is not found,
     * or with status {@code 500 (Internal Server Error)} if the sintomas couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sintomas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Sintomas>> partialUpdateSintomas(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Sintomas sintomas
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sintomas partially : {}, {}", id, sintomas);
        if (sintomas.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sintomas.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sintomasRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Sintomas> result = sintomasRepository
                    .findById(sintomas.getId())
                    .map(existingSintomas -> {
                        if (sintomas.getDescricaoSintoma() != null) {
                            existingSintomas.setDescricaoSintoma(sintomas.getDescricaoSintoma());
                        }

                        return existingSintomas;
                    })
                    .flatMap(sintomasRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /sintomas} : get all the sintomas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sintomas in body.
     */
    @GetMapping("/sintomas")
    public Mono<List<Sintomas>> getAllSintomas() {
        log.debug("REST request to get all Sintomas");
        return sintomasRepository.findAll().collectList();
    }

    /**
     * {@code GET  /sintomas} : get all the sintomas as a stream.
     * @return the {@link Flux} of sintomas.
     */
    @GetMapping(value = "/sintomas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Sintomas> getAllSintomasAsStream() {
        log.debug("REST request to get all Sintomas as a stream");
        return sintomasRepository.findAll();
    }

    /**
     * {@code GET  /sintomas/:id} : get the "id" sintomas.
     *
     * @param id the id of the sintomas to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sintomas, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sintomas/{id}")
    public Mono<ResponseEntity<Sintomas>> getSintomas(@PathVariable Long id) {
        log.debug("REST request to get Sintomas : {}", id);
        Mono<Sintomas> sintomas = sintomasRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sintomas);
    }

    /**
     * {@code DELETE  /sintomas/:id} : delete the "id" sintomas.
     *
     * @param id the id of the sintomas to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sintomas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSintomas(@PathVariable Long id) {
        log.debug("REST request to delete Sintomas : {}", id);
        return sintomasRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
