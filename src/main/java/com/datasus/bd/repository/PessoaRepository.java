package com.datasus.bd.repository;

import com.datasus.bd.domain.Pessoa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pessoa entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaRepository extends ReactiveCrudRepository<Pessoa, Long>, PessoaRepositoryInternal {
    Flux<Pessoa> findAllBy(Pageable pageable);

    @Query("SELECT * FROM pessoa entity WHERE entity.condicao_id_condicao = :id")
    Flux<Pessoa> findByCondicao(Long id);

    @Query("SELECT * FROM pessoa entity WHERE entity.condicao_id_condicao IS NULL")
    Flux<Pessoa> findAllWhereCondicaoIsNull();

    @Override
    <S extends Pessoa> Mono<S> save(S entity);

    @Override
    Flux<Pessoa> findAll();

    @Override
    Mono<Pessoa> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PessoaRepositoryInternal {
    <S extends Pessoa> Mono<S> save(S entity);

    Flux<Pessoa> findAllBy(Pageable pageable);

    Flux<Pessoa> findAll();

    Mono<Pessoa> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pessoa> findAllBy(Pageable pageable, Criteria criteria);

}
