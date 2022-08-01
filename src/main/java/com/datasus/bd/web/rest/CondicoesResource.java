package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Condicoes;
import com.datasus.bd.repository.CondicoesRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Condicoes}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CondicoesResource {

    private final Logger log = LoggerFactory.getLogger(CondicoesResource.class);

    private static final String ENTITY_NAME = "condicoes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CondicoesRepository condicoesRepository;

    public CondicoesResource(CondicoesRepository condicoesRepository) {
        this.condicoesRepository = condicoesRepository;
    }

    /**
     * {@code POST  /condicoes} : Create a new condicoes.
     *
     * @param condicoes the condicoes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new condicoes, or with status {@code 400 (Bad Request)} if the condicoes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/condicoes")
    public Mono<ResponseEntity<Condicoes>> createCondicoes(@RequestBody Condicoes condicoes) throws URISyntaxException {
        log.debug("REST request to save Condicoes : {}", condicoes);
        if (condicoes.getId() != null) {
            throw new BadRequestAlertException("A new condicoes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return condicoesRepository
            .save(condicoes)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/condicoes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /condicoes/:id} : Updates an existing condicoes.
     *
     * @param id the id of the condicoes to save.
     * @param condicoes the condicoes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated condicoes,
     * or with status {@code 400 (Bad Request)} if the condicoes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the condicoes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/condicoes/{id}")
    public Mono<ResponseEntity<Condicoes>> updateCondicoes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Condicoes condicoes
    ) throws URISyntaxException {
        log.debug("REST request to update Condicoes : {}, {}", id, condicoes);
        if (condicoes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, condicoes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return condicoesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return condicoesRepository
                    .save(condicoes)
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
     * {@code PATCH  /condicoes/:id} : Partial updates given fields of an existing condicoes, field will ignore if it is null
     *
     * @param id the id of the condicoes to save.
     * @param condicoes the condicoes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated condicoes,
     * or with status {@code 400 (Bad Request)} if the condicoes is not valid,
     * or with status {@code 404 (Not Found)} if the condicoes is not found,
     * or with status {@code 500 (Internal Server Error)} if the condicoes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/condicoes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Condicoes>> partialUpdateCondicoes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Condicoes condicoes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Condicoes partially : {}, {}", id, condicoes);
        if (condicoes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, condicoes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return condicoesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Condicoes> result = condicoesRepository
                    .findById(condicoes.getId())
                    .map(existingCondicoes -> {
                        if (condicoes.getCondicao() != null) {
                            existingCondicoes.setCondicao(condicoes.getCondicao());
                        }

                        return existingCondicoes;
                    })
                    .flatMap(condicoesRepository::save);

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
     * {@code GET  /condicoes} : get all the condicoes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of condicoes in body.
     */
    @GetMapping("/condicoes")
    public Mono<List<Condicoes>> getAllCondicoes() {
        log.debug("REST request to get all Condicoes");
        return condicoesRepository.findAll().collectList();
    }

    /**
     * {@code GET  /condicoes} : get all the condicoes as a stream.
     * @return the {@link Flux} of condicoes.
     */
    @GetMapping(value = "/condicoes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Condicoes> getAllCondicoesAsStream() {
        log.debug("REST request to get all Condicoes as a stream");
        return condicoesRepository.findAll();
    }

    /**
     * {@code GET  /condicoes/:id} : get the "id" condicoes.
     *
     * @param id the id of the condicoes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the condicoes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/condicoes/{id}")
    public Mono<ResponseEntity<Condicoes>> getCondicoes(@PathVariable Long id) {
        log.debug("REST request to get Condicoes : {}", id);
        Mono<Condicoes> condicoes = condicoesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(condicoes);
    }

    /**
     * {@code DELETE  /condicoes/:id} : delete the "id" condicoes.
     *
     * @param id the id of the condicoes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/condicoes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCondicoes(@PathVariable Long id) {
        log.debug("REST request to delete Condicoes : {}", id);
        return condicoesRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
