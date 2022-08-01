package com.datasus.bd.repository;

import com.datasus.bd.domain.Uf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Uf entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UfRepository extends ReactiveCrudRepository<Uf, Long>, UfRepositoryInternal {
    @Override
    <S extends Uf> Mono<S> save(S entity);

    @Override
    Flux<Uf> findAll();

    @Override
    Mono<Uf> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UfRepositoryInternal {
    <S extends Uf> Mono<S> save(S entity);

    Flux<Uf> findAllBy(Pageable pageable);

    Flux<Uf> findAll();

    Mono<Uf> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Uf> findAllBy(Pageable pageable, Criteria criteria);

}
