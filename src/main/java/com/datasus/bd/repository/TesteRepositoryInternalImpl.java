package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.datasus.bd.domain.Teste;
import com.datasus.bd.repository.rowmapper.PessoaRowMapper;
import com.datasus.bd.repository.rowmapper.SintomasRowMapper;
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
    private final SintomasRowMapper sintomasMapper;
    private final TesteRowMapper testeMapper;

    private static final Table entityTable = Table.aliased("teste", EntityManager.ENTITY_ALIAS);
    private static final Table fkIdTesteTable = Table.aliased("pessoa", "fkIdTeste");
    private static final Table sintomasTable = Table.aliased("sintomas", "sintomas");

    public TesteRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaRowMapper pessoaMapper,
        SintomasRowMapper sintomasMapper,
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
        this.sintomasMapper = sintomasMapper;
        this.testeMapper = testeMapper;
    }

    @Override
    public Flux<Teste> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Teste> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TesteSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PessoaSqlHelper.getColumns(fkIdTesteTable, "fkIdTeste"));
        columns.addAll(SintomasSqlHelper.getColumns(sintomasTable, "sintomas"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(fkIdTesteTable)
            .on(Column.create("fk_id_teste_id_pessoa", entityTable))
            .equals(Column.create("id_pessoa", fkIdTesteTable))
            .leftOuterJoin(sintomasTable)
            .on(Column.create("sintomas_id_sintomas", entityTable))
            .equals(Column.create("id_sintomas", sintomasTable));
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
        Comparison whereClause = Conditions.isEqual(entityTable.column("id_teste"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Teste process(Row row, RowMetadata metadata) {
        Teste entity = testeMapper.apply(row, "e");
        entity.setFkIdTeste(pessoaMapper.apply(row, "fkIdTeste"));
        entity.setSintomas(sintomasMapper.apply(row, "sintomas"));
        return entity;
    }

    @Override
    public <S extends Teste> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
