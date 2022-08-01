package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.datasus.bd.domain.Sintomas;
import com.datasus.bd.domain.Teste;
import com.datasus.bd.repository.rowmapper.MunicipioRowMapper;
import com.datasus.bd.repository.rowmapper.PessoaRowMapper;
import com.datasus.bd.repository.rowmapper.TesteRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Teste entity.
 */
@SuppressWarnings("unused")
class TesteRepositoryInternalImpl extends SimpleR2dbcRepository<Teste, Long> implements TesteRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaRowMapper pessoaMapper;
    private final MunicipioRowMapper municipioMapper;
    private final TesteRowMapper testeMapper;

    private static final Table entityTable = Table.aliased("teste", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");
    private static final Table municipioTable = Table.aliased("municipio", "municipio");

    private static final EntityManager.LinkTable sintomasLink = new EntityManager.LinkTable(
        "rel_teste__sintomas",
        "teste_id",
        "sintomas_id"
    );

    public TesteRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaRowMapper pessoaMapper,
        MunicipioRowMapper municipioMapper,
        TesteRowMapper testeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Teste.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoaMapper = pessoaMapper;
        this.municipioMapper = municipioMapper;
        this.testeMapper = testeMapper;
    }

    @Override
    public Flux<Teste> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Teste> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TesteSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PessoaSqlHelper.getColumns(pessoaTable, "pessoa"));
        columns.addAll(MunicipioSqlHelper.getColumns(municipioTable, "municipio"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pessoaTable)
            .on(Column.create("pessoa_id", entityTable))
            .equals(Column.create("id", pessoaTable))
            .leftOuterJoin(municipioTable)
            .on(Column.create("municipio_id", entityTable))
            .equals(Column.create("id", municipioTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Teste.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Teste> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Teste> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Teste> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Teste> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Teste> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Teste process(Row row, RowMetadata metadata) {
        Teste entity = testeMapper.apply(row, "e");
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        entity.setMunicipio(municipioMapper.apply(row, "municipio"));
        return entity;
    }

    @Override
    public <S extends Teste> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Teste> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(sintomasLink, entity.getId(), entity.getSintomas().stream().map(Sintomas::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(sintomasLink, entityId);
    }
}
