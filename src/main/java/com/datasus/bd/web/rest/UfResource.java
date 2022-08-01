package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Uf;
import com.datasus.bd.repository.UfRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Uf}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UfResource {

    private final Logger log = LoggerFactory.getLogger(UfResource.class);

    private static final String ENTITY_NAME = "uf";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UfRepository ufRepository;

    public UfResource(UfRepository ufRepository) {
        this.ufRepository = ufRepository;
    }

    /**
     * {@code POST  /ufs} : Create a new uf.
     *
     * @param uf the uf to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uf, or with status {@code 400 (Bad Request)} if the uf has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ufs")
    public Mono<ResponseEntity<Uf>> createUf(@RequestBody Uf uf) throws URISyntaxException {
        log.debug("REST request to save Uf : {}", uf);
        if (uf.getId() != null) {
            throw new BadRequestAlertException("A new uf cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ufRepository
            .save(uf)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/ufs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ufs/:id} : Updates an existing uf.
     *
     * @param id the id of the uf to save.
     * @param uf the uf to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uf,
     * or with status {@code 400 (Bad Request)} if the uf is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uf couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ufs/{id}")
    public Mono<ResponseEntity<Uf>> updateUf(@PathVariable(value = "id", required = false) final Long id, @RequestBody Uf uf)
        throws URISyntaxException {
        log.debug("REST request to update Uf : {}, {}", id, uf);
        if (uf.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uf.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ufRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ufRepository
                    .save(uf)
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
     * {@code PATCH  /ufs/:id} : Partial updates given fields of an existing uf, field will ignore if it is null
     *
     * @param id the id of the uf to save.
     * @param uf the uf to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uf,
     * or with status {@code 400 (Bad Request)} if the uf is not valid,
     * or with status {@code 404 (Not Found)} if the uf is not found,
     * or with status {@code 500 (Internal Server Error)} if the uf couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ufs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Uf>> partialUpdateUf(@PathVariable(value = "id", required = false) final Long id, @RequestBody Uf uf)
        throws URISyntaxException {
        log.debug("REST request to partial update Uf partially : {}, {}", id, uf);
        if (uf.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uf.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ufRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Uf> result = ufRepository
                    .findById(uf.getId())
                    .map(existingUf -> {
                        if (uf.getCodigoIbge() != null) {
                            existingUf.setCodigoIbge(uf.getCodigoIbge());
                        }
                        if (uf.getEstado() != null) {
                            existingUf.setEstado(uf.getEstado());
                        }
                        if (uf.getBandeira() != null) {
                            existingUf.setBandeira(uf.getBandeira());
                        }
                        if (uf.getBandeiraContentType() != null) {
                            existingUf.setBandeiraContentType(uf.getBandeiraContentType());
                        }

                        return existingUf;
                    })
                    .flatMap(ufRepository::save);

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
     * {@code GET  /ufs} : get all the ufs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ufs in body.
     */
    @GetMapping("/ufs")
    public Mono<List<Uf>> getAllUfs() {
        log.debug("REST request to get all Ufs");
        return ufRepository.findAll().collectList();
    }

    /**
     * {@code GET  /ufs} : get all the ufs as a stream.
     * @return the {@link Flux} of ufs.
     */
    @GetMapping(value = "/ufs", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Uf> getAllUfsAsStream() {
        log.debug("REST request to get all Ufs as a stream");
        return ufRepository.findAll();
    }

    /**
     * {@code GET  /ufs/:id} : get the "id" uf.
     *
     * @param id the id of the uf to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uf, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ufs/{id}")
    public Mono<ResponseEntity<Uf>> getUf(@PathVariable Long id) {
        log.debug("REST request to get Uf : {}", id);
        Mono<Uf> uf = ufRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uf);
    }

    /**
     * {@code DELETE  /ufs/:id} : delete the "id" uf.
     *
     * @param id the id of the uf to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ufs/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUf(@PathVariable Long id) {
        log.debug("REST request to delete Uf : {}", id);
        return ufRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
