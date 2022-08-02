package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Condicao;
import com.datasus.bd.repository.CondicaoRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Condicao}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CondicaoResource {

    private final Logger log = LoggerFactory.getLogger(CondicaoResource.class);

    private static final String ENTITY_NAME = "condicao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CondicaoRepository condicaoRepository;

    public CondicaoResource(CondicaoRepository condicaoRepository) {
        this.condicaoRepository = condicaoRepository;
    }

    /**
     * {@code POST  /condicaos} : Create a new condicao.
     *
     * @param condicao the condicao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new condicao, or with status {@code 400 (Bad Request)} if the condicao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/condicaos")
    public Mono<ResponseEntity<Condicao>> createCondicao(@RequestBody Condicao condicao) throws URISyntaxException {
        log.debug("REST request to save Condicao : {}", condicao);
        if (condicao.getIdCondicao() != null) {
            throw new BadRequestAlertException("A new condicao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return condicaoRepository
            .save(condicao)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/condicaos/" + result.getIdCondicao()))
                        .headers(
                            HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getIdCondicao().toString())
                        )
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /condicaos/:idCondicao} : Updates an existing condicao.
     *
     * @param idCondicao the id of the condicao to save.
     * @param condicao the condicao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated condicao,
     * or with status {@code 400 (Bad Request)} if the condicao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the condicao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/condicaos/{idCondicao}")
    public Mono<ResponseEntity<Condicao>> updateCondicao(
        @PathVariable(value = "idCondicao", required = false) final Long idCondicao,
        @RequestBody Condicao condicao
    ) throws URISyntaxException {
        log.debug("REST request to update Condicao : {}, {}", idCondicao, condicao);
        if (condicao.getIdCondicao() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idCondicao, condicao.getIdCondicao())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return condicaoRepository
            .existsById(idCondicao)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return condicaoRepository
                    .save(condicao)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getIdCondicao().toString())
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /condicaos/:idCondicao} : Partial updates given fields of an existing condicao, field will ignore if it is null
     *
     * @param idCondicao the id of the condicao to save.
     * @param condicao the condicao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated condicao,
     * or with status {@code 400 (Bad Request)} if the condicao is not valid,
     * or with status {@code 404 (Not Found)} if the condicao is not found,
     * or with status {@code 500 (Internal Server Error)} if the condicao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/condicaos/{idCondicao}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Condicao>> partialUpdateCondicao(
        @PathVariable(value = "idCondicao", required = false) final Long idCondicao,
        @RequestBody Condicao condicao
    ) throws URISyntaxException {
        log.debug("REST request to partial update Condicao partially : {}, {}", idCondicao, condicao);
        if (condicao.getIdCondicao() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idCondicao, condicao.getIdCondicao())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return condicaoRepository
            .existsById(idCondicao)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Condicao> result = condicaoRepository
                    .findById(condicao.getIdCondicao())
                    .map(existingCondicao -> {
                        if (condicao.getCondicao() != null) {
                            existingCondicao.setCondicao(condicao.getCondicao());
                        }

                        return existingCondicao;
                    })
                    .flatMap(condicaoRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getIdCondicao().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /condicaos} : get all the condicaos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of condicaos in body.
     */
    @GetMapping("/condicaos")
    public Mono<ResponseEntity<List<Condicao>>> getAllCondicaos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Condicaos");
        return condicaoRepository
            .count()
            .zipWith(condicaoRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /condicaos/:id} : get the "id" condicao.
     *
     * @param id the id of the condicao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the condicao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/condicaos/{id}")
    public Mono<ResponseEntity<Condicao>> getCondicao(@PathVariable Long id) {
        log.debug("REST request to get Condicao : {}", id);
        Mono<Condicao> condicao = condicaoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(condicao);
    }

    /**
     * {@code DELETE  /condicaos/:id} : delete the "id" condicao.
     *
     * @param id the id of the condicao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/condicaos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCondicao(@PathVariable Long id) {
        log.debug("REST request to delete Condicao : {}", id);
        return condicaoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
