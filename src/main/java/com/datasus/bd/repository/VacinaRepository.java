package com.datasus.bd.repository;

import com.datasus.bd.domain.Vacina;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Vacina entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VacinaRepository extends ReactiveCrudRepository<Vacina, Long>, VacinaRepositoryInternal {
    @Override
    <S extends Vacina> Mono<S> save(S entity);

    @Override
    Flux<Vacina> findAll();

    @Override
    Mono<Vacina> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VacinaRepositoryInternal {
    <S extends Vacina> Mono<S> save(S entity);

    Flux<Vacina> findAllBy(Pageable pageable);

    Flux<Vacina> findAll();

    Mono<Vacina> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Vacina> findAllBy(Pageable pageable, Criteria criteria);

}
