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
    Flux<Teste> findAllBy(Pageable pageable);

    @Query("SELECT * FROM teste entity WHERE entity.fk_id_teste_id_pessoa = :id")
    Flux<Teste> findByFkIdTeste(Long id);

    @Query("SELECT * FROM teste entity WHERE entity.fk_id_teste_id_pessoa IS NULL")
    Flux<Teste> findAllWhereFkIdTesteIsNull();

    @Query("SELECT * FROM teste entity WHERE entity.sintomas_id_sintomas = :id")
    Flux<Teste> findBySintomas(Long id);

    @Query("SELECT * FROM teste entity WHERE entity.sintomas_id_sintomas IS NULL")
    Flux<Teste> findAllWhereSintomasIsNull();

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

}
