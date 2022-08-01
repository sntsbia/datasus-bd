package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.datasus.bd.domain.Condicoes;
import com.datasus.bd.domain.Pessoa;
import com.datasus.bd.domain.enumeration.Sexo;
import com.datasus.bd.repository.rowmapper.PessoaRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Pessoa entity.
 */
@SuppressWarnings("unused")
class PessoaRepositoryInternalImpl extends SimpleR2dbcRepository<Pessoa, Long> implements PessoaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaRowMapper pessoaMapper;

    private static final Table entityTable = Table.aliased("pessoa", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable condicoesLink = new EntityManager.LinkTable(
        "rel_pessoa__condicoes",
        "pessoa_id",
        "condicoes_id"
    );

    public PessoaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaRowMapper pessoaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Pessoa.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoaMapper = pessoaMapper;
    }

    @Override
    public Flux<Pessoa> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Pessoa> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PessoaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Pessoa.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Pessoa> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Pessoa> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Pessoa> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Pessoa> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Pessoa> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Pessoa process(Row row, RowMetadata metadata) {
        Pessoa entity = pessoaMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Pessoa> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Pessoa> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(condicoesLink, entity.getId(), entity.getCondicoes().stream().map(Condicoes::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(condicoesLink, entityId);
    }
}
