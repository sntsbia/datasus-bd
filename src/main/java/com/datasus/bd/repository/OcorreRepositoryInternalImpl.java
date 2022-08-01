package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.datasus.bd.domain.Ocorre;
import com.datasus.bd.repository.rowmapper.MunicipioRowMapper;
import com.datasus.bd.repository.rowmapper.OcorreRowMapper;
import com.datasus.bd.repository.rowmapper.TesteRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Ocorre entity.
 */
@SuppressWarnings("unused")
class OcorreRepositoryInternalImpl extends SimpleR2dbcRepository<Ocorre, Long> implements OcorreRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TesteRowMapper testeMapper;
    private final MunicipioRowMapper municipioMapper;
    private final OcorreRowMapper ocorreMapper;

    private static final Table entityTable = Table.aliased("ocorre", EntityManager.ENTITY_ALIAS);
    private static final Table testeTable = Table.aliased("teste", "teste");
    private static final Table municipioTable = Table.aliased("municipio", "municipio");

    public OcorreRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TesteRowMapper testeMapper,
        MunicipioRowMapper municipioMapper,
        OcorreRowMapper ocorreMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Ocorre.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.testeMapper = testeMapper;
        this.municipioMapper = municipioMapper;
        this.ocorreMapper = ocorreMapper;
    }

    @Override
    public Flux<Ocorre> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Ocorre> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OcorreSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TesteSqlHelper.getColumns(testeTable, "teste"));
        columns.addAll(MunicipioSqlHelper.getColumns(municipioTable, "municipio"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(testeTable)
            .on(Column.create("teste_id", entityTable))
            .equals(Column.create("id", testeTable))
            .leftOuterJoin(municipioTable)
            .on(Column.create("municipio_id", entityTable))
            .equals(Column.create("id", municipioTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Ocorre.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Ocorre> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Ocorre> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Ocorre process(Row row, RowMetadata metadata) {
        Ocorre entity = ocorreMapper.apply(row, "e");
        entity.setTeste(testeMapper.apply(row, "teste"));
        entity.setMunicipio(municipioMapper.apply(row, "municipio"));
        return entity;
    }

    @Override
    public <S extends Ocorre> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
