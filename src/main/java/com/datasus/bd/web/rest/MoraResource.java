package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Mora;
import com.datasus.bd.repository.MoraRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Mora}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MoraResource {

    private final Logger log = LoggerFactory.getLogger(MoraResource.class);

    private static final String ENTITY_NAME = "mora";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MoraRepository moraRepository;

    public MoraResource(MoraRepository moraRepository) {
        this.moraRepository = moraRepository;
    }

    /**
     * {@code POST  /moras} : Create a new mora.
     *
     * @param mora the mora to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mora, or with status {@code 400 (Bad Request)} if the mora has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/moras")
    public Mono<ResponseEntity<Mora>> createMora(@RequestBody Mora mora) throws URISyntaxException {
        log.debug("REST request to save Mora : {}", mora);
        if (mora.getId() != null) {
            throw new BadRequestAlertException("A new mora cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return moraRepository
            .save(mora)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/moras/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /moras/:id} : Updates an existing mora.
     *
     * @param id the id of the mora to save.
     * @param mora the mora to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mora,
     * or with status {@code 400 (Bad Request)} if the mora is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mora couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/moras/{id}")
    public Mono<ResponseEntity<Mora>> updateMora(@PathVariable(value = "id", required = false) final Long id, @RequestBody Mora mora)
        throws URISyntaxException {
        log.debug("REST request to update Mora : {}, {}", id, mora);
        if (mora.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mora.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return moraRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return moraRepository
                    .save(mora)
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
     * {@code PATCH  /moras/:id} : Partial updates given fields of an existing mora, field will ignore if it is null
     *
     * @param id the id of the mora to save.
     * @param mora the mora to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mora,
     * or with status {@code 400 (Bad Request)} if the mora is not valid,
     * or with status {@code 404 (Not Found)} if the mora is not found,
     * or with status {@code 500 (Internal Server Error)} if the mora couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/moras/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Mora>> partialUpdateMora(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Mora mora
    ) throws URISyntaxException {
        log.debug("REST request to partial update Mora partially : {}, {}", id, mora);
        if (mora.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mora.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return moraRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Mora> result = moraRepository
                    .findById(mora.getId())
                    .map(existingMora -> {
                        return existingMora;
                    })
                    .flatMap(moraRepository::save);

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
     * {@code GET  /moras} : get all the moras.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of moras in body.
     */
    @GetMapping("/moras")
    public Mono<ResponseEntity<List<Mora>>> getAllMoras(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Moras");
        return moraRepository
            .count()
            .zipWith(moraRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /moras/:id} : get the "id" mora.
     *
     * @param id the id of the mora to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mora, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/moras/{id}")
    public Mono<ResponseEntity<Mora>> getMora(@PathVariable Long id) {
        log.debug("REST request to get Mora : {}", id);
        Mono<Mora> mora = moraRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mora);
    }

    /**
     * {@code DELETE  /moras/:id} : delete the "id" mora.
     *
     * @param id the id of the mora to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/moras/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteMora(@PathVariable Long id) {
        log.debug("REST request to delete Mora : {}", id);
        return moraRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
