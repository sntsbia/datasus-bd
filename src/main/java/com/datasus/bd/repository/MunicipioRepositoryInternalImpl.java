package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.datasus.bd.domain.Municipio;
import com.datasus.bd.repository.rowmapper.MunicipioRowMapper;
import com.datasus.bd.repository.rowmapper.UfRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Municipio entity.
 */
@SuppressWarnings("unused")
class MunicipioRepositoryInternalImpl extends SimpleR2dbcRepository<Municipio, Long> implements MunicipioRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UfRowMapper ufMapper;
    private final MunicipioRowMapper municipioMapper;

    private static final Table entityTable = Table.aliased("municipio", EntityManager.ENTITY_ALIAS);
    private static final Table ufTable = Table.aliased("uf", "uf");

    public MunicipioRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UfRowMapper ufMapper,
        MunicipioRowMapper municipioMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Municipio.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.ufMapper = ufMapper;
        this.municipioMapper = municipioMapper;
    }

    @Override
    public Flux<Municipio> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Municipio> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MunicipioSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UfSqlHelper.getColumns(ufTable, "uf"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(ufTable)
            .on(Column.create("uf_id", entityTable))
            .equals(Column.create("id", ufTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Municipio.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Municipio> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Municipio> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Municipio process(Row row, RowMetadata metadata) {
        Municipio entity = municipioMapper.apply(row, "e");
        entity.setUf(ufMapper.apply(row, "uf"));
        return entity;
    }

    @Override
    public <S extends Municipio> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
