package com.datasus.bd.repository;

import com.datasus.bd.domain.Sintomas;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Sintomas entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SintomasRepository extends ReactiveCrudRepository<Sintomas, Long>, SintomasRepositoryInternal {
    Flux<Sintomas> findAllBy(Pageable pageable);

    @Override
    <S extends Sintomas> Mono<S> save(S entity);

    @Override
    Flux<Sintomas> findAll();

    @Override
    Mono<Sintomas> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SintomasRepositoryInternal {
    <S extends Sintomas> Mono<S> save(S entity);

    Flux<Sintomas> findAllBy(Pageable pageable);

    Flux<Sintomas> findAll();

    Mono<Sintomas> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Sintomas> findAllBy(Pageable pageable, Criteria criteria);

}
