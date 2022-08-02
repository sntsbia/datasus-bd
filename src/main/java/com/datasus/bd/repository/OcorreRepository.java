package com.datasus.bd.repository;

import com.datasus.bd.domain.Ocorre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Ocorre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OcorreRepository extends ReactiveCrudRepository<Ocorre, Long>, OcorreRepositoryInternal {
    Flux<Ocorre> findAllBy(Pageable pageable);

    @Query("SELECT * FROM ocorre entity WHERE entity.fk_id_teste_id_teste = :id")
    Flux<Ocorre> findByFkIdTeste(Long id);

    @Query("SELECT * FROM ocorre entity WHERE entity.fk_id_teste_id_teste IS NULL")
    Flux<Ocorre> findAllWhereFkIdTesteIsNull();

    @Query("SELECT * FROM ocorre entity WHERE entity.fk_id_municipio_id_municipio = :id")
    Flux<Ocorre> findByFkIdMunicipio(Long id);

    @Query("SELECT * FROM ocorre entity WHERE entity.fk_id_municipio_id_municipio IS NULL")
    Flux<Ocorre> findAllWhereFkIdMunicipioIsNull();

    @Override
    <S extends Ocorre> Mono<S> save(S entity);

    @Override
    Flux<Ocorre> findAll();

    @Override
    Mono<Ocorre> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OcorreRepositoryInternal {
    <S extends Ocorre> Mono<S> save(S entity);

    Flux<Ocorre> findAllBy(Pageable pageable);

    Flux<Ocorre> findAll();

    Mono<Ocorre> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Ocorre> findAllBy(Pageable pageable, Criteria criteria);

}
