package com.datasus.bd.repository;

import com.datasus.bd.domain.Teste;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Teste entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TesteRepository extends ReactiveCrudRepository<Teste, Long>, TesteRepositoryInternal {
    @Override
    Mono<Teste> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Teste> findAllWithEagerRelationships();

    @Override
    Flux<Teste> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM teste entity WHERE entity.pessoa_id = :id")
    Flux<Teste> findByPessoa(Long id);

    @Query("SELECT * FROM teste entity WHERE entity.pessoa_id IS NULL")
    Flux<Teste> findAllWherePessoaIsNull();

    @Query("SELECT * FROM teste entity WHERE entity.municipio_id = :id")
    Flux<Teste> findByMunicipio(Long id);

    @Query("SELECT * FROM teste entity WHERE entity.municipio_id IS NULL")
    Flux<Teste> findAllWhereMunicipioIsNull();

    @Query(
        "SELECT entity.* FROM teste entity JOIN rel_teste__sintomas joinTable ON entity.id = joinTable.sintomas_id WHERE joinTable.sintomas_id = :id"
    )
    Flux<Teste> findBySintomas(Long id);

    @Override
    <S extends Teste> Mono<S> save(S entity);

    @Override
    Flux<Teste> findAll();

    @Override
    Mono<Teste> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TesteRepositoryInternal {
    <S extends Teste> Mono<S> save(S entity);

    Flux<Teste> findAllBy(Pageable pageable);

    Flux<Teste> findAll();

    Mono<Teste> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Teste> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Teste> findOneWithEagerRelationships(Long id);

    Flux<Teste> findAllWithEagerRelationships();

    Flux<Teste> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
