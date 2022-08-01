package com.datasus.bd.repository;

import com.datasus.bd.domain.Toma;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Toma entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TomaRepository extends ReactiveCrudRepository<Toma, Long>, TomaRepositoryInternal {
    @Query("SELECT * FROM toma entity WHERE entity.vacina_id = :id")
    Flux<Toma> findByVacina(Long id);

    @Query("SELECT * FROM toma entity WHERE entity.vacina_id IS NULL")
    Flux<Toma> findAllWhereVacinaIsNull();

    @Query("SELECT * FROM toma entity WHERE entity.pessoa_id = :id")
    Flux<Toma> findByPessoa(Long id);

    @Query("SELECT * FROM toma entity WHERE entity.pessoa_id IS NULL")
    Flux<Toma> findAllWherePessoaIsNull();

    @Override
    <S extends Toma> Mono<S> save(S entity);

    @Override
    Flux<Toma> findAll();

    @Override
    Mono<Toma> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TomaRepositoryInternal {
    <S extends Toma> Mono<S> save(S entity);

    Flux<Toma> findAllBy(Pageable pageable);

    Flux<Toma> findAll();

    Mono<Toma> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Toma> findAllBy(Pageable pageable, Criteria criteria);

}
