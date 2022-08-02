package com.datasus.bd.repository;

import com.datasus.bd.domain.Condicao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Condicao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CondicaoRepository extends ReactiveCrudRepository<Condicao, Long>, CondicaoRepositoryInternal {
    Flux<Condicao> findAllBy(Pageable pageable);

    @Override
    <S extends Condicao> Mono<S> save(S entity);

    @Override
    Flux<Condicao> findAll();

    @Override
    Mono<Condicao> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CondicaoRepositoryInternal {
    <S extends Condicao> Mono<S> save(S entity);

    Flux<Condicao> findAllBy(Pageable pageable);

    Flux<Condicao> findAll();

    Mono<Condicao> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Condicao> findAllBy(Pageable pageable, Criteria criteria);

}
