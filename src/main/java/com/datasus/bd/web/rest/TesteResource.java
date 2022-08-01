package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Teste;
import com.datasus.bd.repository.TesteRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Teste}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TesteResource {

    private final Logger log = LoggerFactory.getLogger(TesteResource.class);

    private static final String ENTITY_NAME = "teste";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TesteRepository testeRepository;

    public TesteResource(TesteRepository testeRepository) {
        this.testeRepository = testeRepository;
    }

    /**
     * {@code POST  /testes} : Create a new teste.
     *
     * @param teste the teste to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teste, or with status {@code 400 (Bad Request)} if the teste has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/testes")
    public Mono<ResponseEntity<Teste>> createTeste(@RequestBody Teste teste) throws URISyntaxException {
        log.debug("REST request to save Teste : {}", teste);
        if (teste.getId() != null) {
            throw new BadRequestAlertException("A new teste cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return testeRepository
            .save(teste)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/testes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /testes/:id} : Updates an existing teste.
     *
     * @param id the id of the teste to save.
     * @param teste the teste to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teste,
     * or with status {@code 400 (Bad Request)} if the teste is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teste couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/testes/{id}")
    public Mono<ResponseEntity<Teste>> updateTeste(@PathVariable(value = "id", required = false) final Long id, @RequestBody Teste teste)
        throws URISyntaxException {
        log.debug("REST request to update Teste : {}, {}", id, teste);
        if (teste.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teste.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return testeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return testeRepository
                    .save(teste)
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
     * {@code PATCH  /testes/:id} : Partial updates given fields of an existing teste, field will ignore if it is null
     *
     * @param id the id of the teste to save.
     * @param teste the teste to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teste,
     * or with status {@code 400 (Bad Request)} if the teste is not valid,
     * or with status {@code 404 (Not Found)} if the teste is not found,
     * or with status {@code 500 (Internal Server Error)} if the teste couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/testes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Teste>> partialUpdateTeste(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Teste teste
    ) throws URISyntaxException {
        log.debug("REST request to partial update Teste partially : {}, {}", id, teste);
        if (teste.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teste.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return testeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Teste> result = testeRepository
                    .findById(teste.getId())
                    .map(existingTeste -> {
                        if (teste.getDataTeste() != null) {
                            existingTeste.setDataTeste(teste.getDataTeste());
                        }
                        if (teste.getResultado() != null) {
                            existingTeste.setResultado(teste.getResultado());
                        }

                        return existingTeste;
                    })
                    .flatMap(testeRepository::save);

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
     * {@code GET  /testes} : get all the testes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testes in body.
     */
    @GetMapping("/testes")
    public Mono<List<Teste>> getAllTestes(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Testes");
        return testeRepository.findAllWithEagerRelationships().collectList();
    }

    /**
     * {@code GET  /testes} : get all the testes as a stream.
     * @return the {@link Flux} of testes.
     */
    @GetMapping(value = "/testes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Teste> getAllTestesAsStream() {
        log.debug("REST request to get all Testes as a stream");
        return testeRepository.findAll();
    }

    /**
     * {@code GET  /testes/:id} : get the "id" teste.
     *
     * @param id the id of the teste to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teste, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/testes/{id}")
    public Mono<ResponseEntity<Teste>> getTeste(@PathVariable Long id) {
        log.debug("REST request to get Teste : {}", id);
        Mono<Teste> teste = testeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(teste);
    }

    /**
     * {@code DELETE  /testes/:id} : delete the "id" teste.
     *
     * @param id the id of the teste to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/testes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTeste(@PathVariable Long id) {
        log.debug("REST request to delete Teste : {}", id);
        return testeRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
