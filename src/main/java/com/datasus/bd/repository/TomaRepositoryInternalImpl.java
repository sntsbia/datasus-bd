package com.datasus.bd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.datasus.bd.domain.Toma;
import com.datasus.bd.repository.rowmapper.PessoaRowMapper;
import com.datasus.bd.repository.rowmapper.TomaRowMapper;
import com.datasus.bd.repository.rowmapper.VacinaRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Toma entity.
 */
@SuppressWarnings("unused")
class TomaRepositoryInternalImpl extends SimpleR2dbcRepository<Toma, Long> implements TomaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final VacinaRowMapper vacinaMapper;
    private final PessoaRowMapper pessoaMapper;
    private final TomaRowMapper tomaMapper;

    private static final Table entityTable = Table.aliased("toma", EntityManager.ENTITY_ALIAS);
    private static final Table fkIdVacinaTable = Table.aliased("vacina", "fkIdVacina");
    private static final Table fkIdPessoaTable = Table.aliased("pessoa", "fkIdPessoa");

    public TomaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        VacinaRowMapper vacinaMapper,
        PessoaRowMapper pessoaMapper,
        TomaRowMapper tomaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Toma.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.vacinaMapper = vacinaMapper;
        this.pessoaMapper = pessoaMapper;
        this.tomaMapper = tomaMapper;
    }

    @Override
    public Flux<Toma> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Toma> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TomaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(VacinaSqlHelper.getColumns(fkIdVacinaTable, "fkIdVacina"));
        columns.addAll(PessoaSqlHelper.getColumns(fkIdPessoaTable, "fkIdPessoa"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(fkIdVacinaTable)
            .on(Column.create("fk_id_vacina_id_vacina", entityTable))
            .equals(Column.create("id_vacina", fkIdVacinaTable))
            .leftOuterJoin(fkIdPessoaTable)
            .on(Column.create("fk_id_pessoa_id_pessoa", entityTable))
            .equals(Column.create("id_pessoa", fkIdPessoaTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Toma.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Toma> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Toma> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Toma process(Row row, RowMetadata metadata) {
        Toma entity = tomaMapper.apply(row, "e");
        entity.setFkIdVacina(vacinaMapper.apply(row, "fkIdVacina"));
        entity.setFkIdPessoa(pessoaMapper.apply(row, "fkIdPessoa"));
        return entity;
    }

    @Override
    public <S extends Toma> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
