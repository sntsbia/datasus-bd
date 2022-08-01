package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Vacina;
import com.datasus.bd.repository.VacinaRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Vacina}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VacinaResource {

    private final Logger log = LoggerFactory.getLogger(VacinaResource.class);

    private static final String ENTITY_NAME = "vacina";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VacinaRepository vacinaRepository;

    public VacinaResource(VacinaRepository vacinaRepository) {
        this.vacinaRepository = vacinaRepository;
    }

    /**
     * {@code POST  /vacinas} : Create a new vacina.
     *
     * @param vacina the vacina to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vacina, or with status {@code 400 (Bad Request)} if the vacina has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vacinas")
    public Mono<ResponseEntity<Vacina>> createVacina(@RequestBody Vacina vacina) throws URISyntaxException {
        log.debug("REST request to save Vacina : {}", vacina);
        if (vacina.getId() != null) {
            throw new BadRequestAlertException("A new vacina cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return vacinaRepository
            .save(vacina)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/vacinas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /vacinas/:id} : Updates an existing vacina.
     *
     * @param id the id of the vacina to save.
     * @param vacina the vacina to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vacina,
     * or with status {@code 400 (Bad Request)} if the vacina is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vacina couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vacinas/{id}")
    public Mono<ResponseEntity<Vacina>> updateVacina(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Vacina vacina
    ) throws URISyntaxException {
        log.debug("REST request to update Vacina : {}, {}", id, vacina);
        if (vacina.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vacina.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return vacinaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return vacinaRepository
                    .save(vacina)
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
     * {@code PATCH  /vacinas/:id} : Partial updates given fields of an existing vacina, field will ignore if it is null
     *
     * @param id the id of the vacina to save.
     * @param vacina the vacina to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vacina,
     * or with status {@code 400 (Bad Request)} if the vacina is not valid,
     * or with status {@code 404 (Not Found)} if the vacina is not found,
     * or with status {@code 500 (Internal Server Error)} if the vacina couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/vacinas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Vacina>> partialUpdateVacina(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Vacina vacina
    ) throws URISyntaxException {
        log.debug("REST request to partial update Vacina partially : {}, {}", id, vacina);
        if (vacina.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vacina.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return vacinaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Vacina> result = vacinaRepository
                    .findById(vacina.getId())
                    .map(existingVacina -> {
                        if (vacina.getFabricante() != null) {
                            existingVacina.setFabricante(vacina.getFabricante());
                        }
                        if (vacina.getNomeVacina() != null) {
                            existingVacina.setNomeVacina(vacina.getNomeVacina());
                        }

                        return existingVacina;
                    })
                    .flatMap(vacinaRepository::save);

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
     * {@code GET  /vacinas} : get all the vacinas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vacinas in body.
     */
    @GetMapping("/vacinas")
    public Mono<List<Vacina>> getAllVacinas() {
        log.debug("REST request to get all Vacinas");
        return vacinaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /vacinas} : get all the vacinas as a stream.
     * @return the {@link Flux} of vacinas.
     */
    @GetMapping(value = "/vacinas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Vacina> getAllVacinasAsStream() {
        log.debug("REST request to get all Vacinas as a stream");
        return vacinaRepository.findAll();
    }

    /**
     * {@code GET  /vacinas/:id} : get the "id" vacina.
     *
     * @param id the id of the vacina to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vacina, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vacinas/{id}")
    public Mono<ResponseEntity<Vacina>> getVacina(@PathVariable Long id) {
        log.debug("REST request to get Vacina : {}", id);
        Mono<Vacina> vacina = vacinaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(vacina);
    }

    /**
     * {@code DELETE  /vacinas/:id} : delete the "id" vacina.
     *
     * @param id the id of the vacina to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vacinas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteVacina(@PathVariable Long id) {
        log.debug("REST request to delete Vacina : {}", id);
        return vacinaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
