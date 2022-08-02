package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Municipio;
import com.datasus.bd.repository.MunicipioRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Municipio}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MunicipioResource {

    private final Logger log = LoggerFactory.getLogger(MunicipioResource.class);

    private static final String ENTITY_NAME = "municipio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MunicipioRepository municipioRepository;

    public MunicipioResource(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    /**
     * {@code POST  /municipios} : Create a new municipio.
     *
     * @param municipio the municipio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new municipio, or with status {@code 400 (Bad Request)} if the municipio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/municipios")
    public Mono<ResponseEntity<Municipio>> createMunicipio(@RequestBody Municipio municipio) throws URISyntaxException {
        log.debug("REST request to save Municipio : {}", municipio);
        if (municipio.getIdMunicipio() != null) {
            throw new BadRequestAlertException("A new municipio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return municipioRepository
            .save(municipio)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/municipios/" + result.getIdMunicipio()))
                        .headers(
                            HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getIdMunicipio().toString())
                        )
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /municipios/:idMunicipio} : Updates an existing municipio.
     *
     * @param idMunicipio the id of the municipio to save.
     * @param municipio the municipio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated municipio,
     * or with status {@code 400 (Bad Request)} if the municipio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the municipio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/municipios/{idMunicipio}")
    public Mono<ResponseEntity<Municipio>> updateMunicipio(
        @PathVariable(value = "idMunicipio", required = false) final Long idMunicipio,
        @RequestBody Municipio municipio
    ) throws URISyntaxException {
        log.debug("REST request to update Municipio : {}, {}", idMunicipio, municipio);
        if (municipio.getIdMunicipio() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idMunicipio, municipio.getIdMunicipio())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return municipioRepository
            .existsById(idMunicipio)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return municipioRepository
                    .save(municipio)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getIdMunicipio().toString())
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /municipios/:idMunicipio} : Partial updates given fields of an existing municipio, field will ignore if it is null
     *
     * @param idMunicipio the id of the municipio to save.
     * @param municipio the municipio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated municipio,
     * or with status {@code 400 (Bad Request)} if the municipio is not valid,
     * or with status {@code 404 (Not Found)} if the municipio is not found,
     * or with status {@code 500 (Internal Server Error)} if the municipio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/municipios/{idMunicipio}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Municipio>> partialUpdateMunicipio(
        @PathVariable(value = "idMunicipio", required = false) final Long idMunicipio,
        @RequestBody Municipio municipio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Municipio partially : {}, {}", idMunicipio, municipio);
        if (municipio.getIdMunicipio() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idMunicipio, municipio.getIdMunicipio())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return municipioRepository
            .existsById(idMunicipio)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Municipio> result = municipioRepository
                    .findById(municipio.getIdMunicipio())
                    .map(existingMunicipio -> {
                        if (municipio.getMunicipio() != null) {
                            existingMunicipio.setMunicipio(municipio.getMunicipio());
                        }

                        return existingMunicipio;
                    })
                    .flatMap(municipioRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getIdMunicipio().toString())
                            )
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /municipios} : get all the municipios.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of municipios in body.
     */
    @GetMapping("/municipios")
    public Mono<ResponseEntity<List<Municipio>>> getAllMunicipios(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Municipios");
        return municipioRepository
            .count()
            .zipWith(municipioRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /municipios/:id} : get the "id" municipio.
     *
     * @param id the id of the municipio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the municipio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/municipios/{id}")
    public Mono<ResponseEntity<Municipio>> getMunicipio(@PathVariable Long id) {
        log.debug("REST request to get Municipio : {}", id);
        Mono<Municipio> municipio = municipioRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(municipio);
    }

    /**
     * {@code DELETE  /municipios/:id} : delete the "id" municipio.
     *
     * @param id the id of the municipio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/municipios/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteMunicipio(@PathVariable Long id) {
        log.debug("REST request to delete Municipio : {}", id);
        return municipioRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
