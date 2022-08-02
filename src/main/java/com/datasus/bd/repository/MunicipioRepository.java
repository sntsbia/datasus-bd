package com.datasus.bd.repository;

import com.datasus.bd.domain.Municipio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Municipio entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MunicipioRepository extends ReactiveCrudRepository<Municipio, Long>, MunicipioRepositoryInternal {
    Flux<Municipio> findAllBy(Pageable pageable);

    @Query("SELECT * FROM municipio entity WHERE entity.fk_id_uf_id_uf = :id")
    Flux<Municipio> findByFkIdUf(Long id);

    @Query("SELECT * FROM municipio entity WHERE entity.fk_id_uf_id_uf IS NULL")
    Flux<Municipio> findAllWhereFkIdUfIsNull();

    @Override
    <S extends Municipio> Mono<S> save(S entity);

    @Override
    Flux<Municipio> findAll();

    @Override
    Mono<Municipio> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MunicipioRepositoryInternal {
    <S extends Municipio> Mono<S> save(S entity);

    Flux<Municipio> findAllBy(Pageable pageable);

    Flux<Municipio> findAll();

    Mono<Municipio> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Municipio> findAllBy(Pageable pageable, Criteria criteria);

}
