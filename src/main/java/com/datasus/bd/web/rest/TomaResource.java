package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Toma;
import com.datasus.bd.repository.TomaRepository;
import com.datasus.bd.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.datasus.bd.domain.Toma}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TomaResource {

    private final Logger log = LoggerFactory.getLogger(TomaResource.class);

    private static final String ENTITY_NAME = "toma";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TomaRepository tomaRepository;

    public TomaResource(TomaRepository tomaRepository) {
        this.tomaRepository = tomaRepository;
    }

    /**
     * {@code POST  /tomas} : Create a new toma.
     *
     * @param toma the toma to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new toma, or with status {@code 400 (Bad Request)} if the toma has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tomas")
    public Mono<ResponseEntity<Toma>> createToma(@RequestBody Toma toma) throws URISyntaxException {
        log.debug("REST request to save Toma : {}", toma);
        if (toma.getId() != null) {
            throw new BadRequestAlertException("A new toma cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tomaRepository
            .save(toma)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tomas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tomas/:id} : Updates an existing toma.
     *
     * @param id the id of the toma to save.
     * @param toma the toma to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toma,
     * or with status {@code 400 (Bad Request)} if the toma is not valid,
     * or with status {@code 500 (Internal Server Error)} if the toma couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tomas/{id}")
    public Mono<ResponseEntity<Toma>> updateToma(@PathVariable(value = "id", required = false) final Long id, @RequestBody Toma toma)
        throws URISyntaxException {
        log.debug("REST request to update Toma : {}, {}", id, toma);
        if (toma.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, toma.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tomaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tomaRepository
                    .save(toma)
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
     * {@code PATCH  /tomas/:id} : Partial updates given fields of an existing toma, field will ignore if it is null
     *
     * @param id the id of the toma to save.
     * @param toma the toma to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toma,
     * or with status {@code 400 (Bad Request)} if the toma is not valid,
     * or with status {@code 404 (Not Found)} if the toma is not found,
     * or with status {@code 500 (Internal Server Error)} if the toma couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tomas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Toma>> partialUpdateToma(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Toma toma
    ) throws URISyntaxException {
        log.debug("REST request to partial update Toma partially : {}, {}", id, toma);
        if (toma.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, toma.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tomaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Toma> result = tomaRepository
                    .findById(toma.getId())
                    .map(existingToma -> {
                        if (toma.getData() != null) {
                            existingToma.setData(toma.getData());
                        }
                        if (toma.getLote() != null) {
                            existingToma.setLote(toma.getLote());
                        }
                        if (toma.getDose() != null) {
                            existingToma.setDose(toma.getDose());
                        }

                        return existingToma;
                    })
                    .flatMap(tomaRepository::save);

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
     * {@code GET  /tomas} : get all the tomas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tomas in body.
     */
    @GetMapping("/tomas")
    public Mono<ResponseEntity<List<Toma>>> getAllTomas(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Tomas");
        return tomaRepository
            .count()
            .zipWith(tomaRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /tomas/:id} : get the "id" toma.
     *
     * @param id the id of the toma to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the toma, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tomas/{id}")
    public Mono<ResponseEntity<Toma>> getToma(@PathVariable Long id) {
        log.debug("REST request to get Toma : {}", id);
        Mono<Toma> toma = tomaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(toma);
    }

    /**
     * {@code DELETE  /tomas/:id} : delete the "id" toma.
     *
     * @param id the id of the toma to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tomas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteToma(@PathVariable Long id) {
        log.debug("REST request to delete Toma : {}", id);
        return tomaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
