package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Ocorre;
import com.datasus.bd.repository.OcorreRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Ocorre}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OcorreResource {

    private final Logger log = LoggerFactory.getLogger(OcorreResource.class);

    private static final String ENTITY_NAME = "ocorre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OcorreRepository ocorreRepository;

    public OcorreResource(OcorreRepository ocorreRepository) {
        this.ocorreRepository = ocorreRepository;
    }

    /**
     * {@code POST  /ocorres} : Create a new ocorre.
     *
     * @param ocorre the ocorre to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ocorre, or with status {@code 400 (Bad Request)} if the ocorre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ocorres")
    public Mono<ResponseEntity<Ocorre>> createOcorre(@RequestBody Ocorre ocorre) throws URISyntaxException {
        log.debug("REST request to save Ocorre : {}", ocorre);
        if (ocorre.getId() != null) {
            throw new BadRequestAlertException("A new ocorre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ocorreRepository
            .save(ocorre)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/ocorres/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ocorres/:id} : Updates an existing ocorre.
     *
     * @param id the id of the ocorre to save.
     * @param ocorre the ocorre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ocorre,
     * or with status {@code 400 (Bad Request)} if the ocorre is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ocorre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ocorres/{id}")
    public Mono<ResponseEntity<Ocorre>> updateOcorre(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Ocorre ocorre
    ) throws URISyntaxException {
        log.debug("REST request to update Ocorre : {}, {}", id, ocorre);
        if (ocorre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ocorre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ocorreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ocorreRepository
                    .save(ocorre)
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
     * {@code PATCH  /ocorres/:id} : Partial updates given fields of an existing ocorre, field will ignore if it is null
     *
     * @param id the id of the ocorre to save.
     * @param ocorre the ocorre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ocorre,
     * or with status {@code 400 (Bad Request)} if the ocorre is not valid,
     * or with status {@code 404 (Not Found)} if the ocorre is not found,
     * or with status {@code 500 (Internal Server Error)} if the ocorre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ocorres/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Ocorre>> partialUpdateOcorre(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Ocorre ocorre
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ocorre partially : {}, {}", id, ocorre);
        if (ocorre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ocorre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ocorreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Ocorre> result = ocorreRepository
                    .findById(ocorre.getId())
                    .map(existingOcorre -> {
                        return existingOcorre;
                    })
                    .flatMap(ocorreRepository::save);

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
     * {@code GET  /ocorres} : get all the ocorres.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ocorres in body.
     */
    @GetMapping("/ocorres")
    public Mono<ResponseEntity<List<Ocorre>>> getAllOcorres(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Ocorres");
        return ocorreRepository
            .count()
            .zipWith(ocorreRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /ocorres/:id} : get the "id" ocorre.
     *
     * @param id the id of the ocorre to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ocorre, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ocorres/{id}")
    public Mono<ResponseEntity<Ocorre>> getOcorre(@PathVariable Long id) {
        log.debug("REST request to get Ocorre : {}", id);
        Mono<Ocorre> ocorre = ocorreRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ocorre);
    }

    /**
     * {@code DELETE  /ocorres/:id} : delete the "id" ocorre.
     *
     * @param id the id of the ocorre to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ocorres/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOcorre(@PathVariable Long id) {
        log.debug("REST request to delete Ocorre : {}", id);
        return ocorreRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
