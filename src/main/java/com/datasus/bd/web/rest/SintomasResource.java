package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Sintomas;
import com.datasus.bd.repository.SintomasRepository;
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
        if (sintomas.getIdSintomas() != null) {
            throw new BadRequestAlertException("A new sintomas cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return sintomasRepository
            .save(sintomas)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/sintomas/" + result.getIdSintomas()))
                        .headers(
                            HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getIdSintomas().toString())
                        )
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /sintomas/:idSintomas} : Updates an existing sintomas.
     *
     * @param idSintomas the id of the sintomas to save.
     * @param sintomas the sintomas to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sintomas,
     * or with status {@code 400 (Bad Request)} if the sintomas is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sintomas couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sintomas/{idSintomas}")
    public Mono<ResponseEntity<Sintomas>> updateSintomas(
        @PathVariable(value = "idSintomas", required = false) final Long idSintomas,
        @RequestBody Sintomas sintomas
    ) throws URISyntaxException {
        log.debug("REST request to update Sintomas : {}, {}", idSintomas, sintomas);
        if (sintomas.getIdSintomas() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idSintomas, sintomas.getIdSintomas())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sintomasRepository
            .existsById(idSintomas)
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
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getIdSintomas().toString())
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /sintomas/:idSintomas} : Partial updates given fields of an existing sintomas, field will ignore if it is null
     *
     * @param idSintomas the id of the sintomas to save.
     * @param sintomas the sintomas to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sintomas,
     * or with status {@code 400 (Bad Request)} if the sintomas is not valid,
     * or with status {@code 404 (Not Found)} if the sintomas is not found,
     * or with status {@code 500 (Internal Server Error)} if the sintomas couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sintomas/{idSintomas}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Sintomas>> partialUpdateSintomas(
        @PathVariable(value = "idSintomas", required = false) final Long idSintomas,
        @RequestBody Sintomas sintomas
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sintomas partially : {}, {}", idSintomas, sintomas);
        if (sintomas.getIdSintomas() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idSintomas, sintomas.getIdSintomas())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sintomasRepository
            .existsById(idSintomas)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Sintomas> result = sintomasRepository
                    .findById(sintomas.getIdSintomas())
                    .map(existingSintomas -> {
                        if (sintomas.getSintomas() != null) {
                            existingSintomas.setSintomas(sintomas.getSintomas());
                        }

                        return existingSintomas;
                    })
                    .flatMap(sintomasRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getIdSintomas().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /sintomas} : get all the sintomas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sintomas in body.
     */
    @GetMapping("/sintomas")
    public Mono<ResponseEntity<List<Sintomas>>> getAllSintomas(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Sintomas");
        return sintomasRepository
            .count()
            .zipWith(sintomasRepository.findAllBy(pageable).collectList())
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
