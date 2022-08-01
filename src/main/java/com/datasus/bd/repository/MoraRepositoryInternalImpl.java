package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.datasus.bd.domain.Mora;
import com.datasus.bd.repository.rowmapper.MoraRowMapper;
import com.datasus.bd.repository.rowmapper.MunicipioRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Mora entity.
 */
@SuppressWarnings("unused")
class MoraRepositoryInternalImpl extends SimpleR2dbcRepository<Mora, Long> implements MoraRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaRowMapper pessoaMapper;
    private final MunicipioRowMapper municipioMapper;
    private final MoraRowMapper moraMapper;

    private static final Table entityTable = Table.aliased("mora", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");
    private static final Table municipioTable = Table.aliased("municipio", "municipio");

    public MoraRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaRowMapper pessoaMapper,
        MunicipioRowMapper municipioMapper,
        MoraRowMapper moraMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Mora.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoaMapper = pessoaMapper;
        this.municipioMapper = municipioMapper;
        this.moraMapper = moraMapper;
    }

    @Override
    public Flux<Mora> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Mora> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MoraSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
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
        String select = entityManager.createSelect(selectFrom, Mora.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Mora> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Mora> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Mora process(Row row, RowMetadata metadata) {
        Mora entity = moraMapper.apply(row, "e");
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        entity.setMunicipio(municipioMapper.apply(row, "municipio"));
        return entity;
    }

    @Override
    public <S extends Mora> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
