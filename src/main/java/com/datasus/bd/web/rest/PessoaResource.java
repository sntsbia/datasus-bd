package com.datasus.bd.web.rest;

import com.datasus.bd.domain.Pessoa;
import com.datasus.bd.repository.PessoaRepository;
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
 * REST controller for managing {@link com.datasus.bd.domain.Pessoa}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaResource.class);

    private static final String ENTITY_NAME = "pessoa";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaRepository pessoaRepository;

    public PessoaResource(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    /**
     * {@code POST  /pessoas} : Create a new pessoa.
     *
     * @param pessoa the pessoa to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoa, or with status {@code 400 (Bad Request)} if the pessoa has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoas")
    public Mono<ResponseEntity<Pessoa>> createPessoa(@RequestBody Pessoa pessoa) throws URISyntaxException {
        log.debug("REST request to save Pessoa : {}", pessoa);
        if (pessoa.getIdPessoa() != null) {
            throw new BadRequestAlertException("A new pessoa cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaRepository
            .save(pessoa)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/pessoas/" + result.getIdPessoa()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getIdPessoa().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /pessoas/:idPessoa} : Updates an existing pessoa.
     *
     * @param idPessoa the id of the pessoa to save.
     * @param pessoa the pessoa to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoa,
     * or with status {@code 400 (Bad Request)} if the pessoa is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoa couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoas/{idPessoa}")
    public Mono<ResponseEntity<Pessoa>> updatePessoa(
        @PathVariable(value = "idPessoa", required = false) final Long idPessoa,
        @RequestBody Pessoa pessoa
    ) throws URISyntaxException {
        log.debug("REST request to update Pessoa : {}, {}", idPessoa, pessoa);
        if (pessoa.getIdPessoa() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idPessoa, pessoa.getIdPessoa())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaRepository
            .existsById(idPessoa)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return pessoaRepository
                    .save(pessoa)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getIdPessoa().toString())
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /pessoas/:idPessoa} : Partial updates given fields of an existing pessoa, field will ignore if it is null
     *
     * @param idPessoa the id of the pessoa to save.
     * @param pessoa the pessoa to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoa,
     * or with status {@code 400 (Bad Request)} if the pessoa is not valid,
     * or with status {@code 404 (Not Found)} if the pessoa is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoa couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoas/{idPessoa}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pessoa>> partialUpdatePessoa(
        @PathVariable(value = "idPessoa", required = false) final Long idPessoa,
        @RequestBody Pessoa pessoa
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pessoa partially : {}, {}", idPessoa, pessoa);
        if (pessoa.getIdPessoa() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idPessoa, pessoa.getIdPessoa())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaRepository
            .existsById(idPessoa)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pessoa> result = pessoaRepository
                    .findById(pessoa.getIdPessoa())
                    .map(existingPessoa -> {
                        if (pessoa.getSexo() != null) {
                            existingPessoa.setSexo(pessoa.getSexo());
                        }
                        if (pessoa.getIdade() != null) {
                            existingPessoa.setIdade(pessoa.getIdade());
                        }

                        return existingPessoa;
                    })
                    .flatMap(pessoaRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getIdPessoa().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /pessoas} : get all the pessoas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas")
    public Mono<ResponseEntity<List<Pessoa>>> getAllPessoas(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Pessoas");
        return pessoaRepository
            .count()
            .zipWith(pessoaRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /pessoas/:id} : get the "id" pessoa.
     *
     * @param id the id of the pessoa to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoa, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoas/{id}")
    public Mono<ResponseEntity<Pessoa>> getPessoa(@PathVariable Long id) {
        log.debug("REST request to get Pessoa : {}", id);
        Mono<Pessoa> pessoa = pessoaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoa);
    }

    /**
     * {@code DELETE  /pessoas/:id} : delete the "id" pessoa.
     *
     * @param id the id of the pessoa to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoa(@PathVariable Long id) {
        log.debug("REST request to delete Pessoa : {}", id);
        return pessoaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
