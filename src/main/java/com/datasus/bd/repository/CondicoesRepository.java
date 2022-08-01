package com.datasus.bd.repository;

import com.datasus.bd.domain.Condicoes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Condicoes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CondicoesRepository extends ReactiveCrudRepository<Condicoes, Long>, CondicoesRepositoryInternal {
    @Override
    <S extends Condicoes> Mono<S> save(S entity);

    @Override
    Flux<Condicoes> findAll();

    @Override
    Mono<Condicoes> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CondicoesRepositoryInternal {
    <S extends Condicoes> Mono<S> save(S entity);

    Flux<Condicoes> findAllBy(Pageable pageable);

    Flux<Condicoes> findAll();

    Mono<Condicoes> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Condicoes> findAllBy(Pageable pageable, Criteria criteria);

}
