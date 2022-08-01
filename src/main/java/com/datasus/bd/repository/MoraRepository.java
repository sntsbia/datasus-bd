package com.datasus.bd.repository;

import com.datasus.bd.domain.Mora;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Mora entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MoraRepository extends ReactiveCrudRepository<Mora, Long>, MoraRepositoryInternal {
    @Query("SELECT * FROM mora entity WHERE entity.pessoa_id = :id")
    Flux<Mora> findByPessoa(Long id);

    @Query("SELECT * FROM mora entity WHERE entity.pessoa_id IS NULL")
    Flux<Mora> findAllWherePessoaIsNull();

    @Query("SELECT * FROM mora entity WHERE entity.municipio_id = :id")
    Flux<Mora> findByMunicipio(Long id);

    @Query("SELECT * FROM mora entity WHERE entity.municipio_id IS NULL")
    Flux<Mora> findAllWhereMunicipioIsNull();

    @Override
    <S extends Mora> Mono<S> save(S entity);

    @Override
    Flux<Mora> findAll();

    @Override
    Mono<Mora> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MoraRepositoryInternal {
    <S extends Mora> Mono<S> save(S entity);

    Flux<Mora> findAllBy(Pageable pageable);

    Flux<Mora> findAll();

    Mono<Mora> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Mora> findAllBy(Pageable pageable, Criteria criteria);

}
