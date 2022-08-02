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
    Flux<Toma> findAllBy(Pageable pageable);

    @Query("SELECT * FROM toma entity WHERE entity.fk_id_vacina_id_vacina = :id")
    Flux<Toma> findByFkIdVacina(Long id);

    @Query("SELECT * FROM toma entity WHERE entity.fk_id_vacina_id_vacina IS NULL")
    Flux<Toma> findAllWhereFkIdVacinaIsNull();

    @Query("SELECT * FROM toma entity WHERE entity.fk_id_pessoa_id_pessoa = :id")
    Flux<Toma> findByFkIdPessoa(Long id);

    @Query("SELECT * FROM toma entity WHERE entity.fk_id_pessoa_id_pessoa IS NULL")
    Flux<Toma> findAllWhereFkIdPessoaIsNull();

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
