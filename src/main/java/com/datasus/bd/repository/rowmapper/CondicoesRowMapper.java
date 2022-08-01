package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Condicoes;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Condicoes}, with proper type conversions.
 */
@Service
public class CondicoesRowMapper implements BiFunction<Row, String, Condicoes> {

    private final ColumnConverter converter;

    public CondicoesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Condicoes} stored in the database.
     */
    @Override
    public Condicoes apply(Row row, String prefix) {
        Condicoes entity = new Condicoes();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCondicao(converter.fromRow(row, prefix + "_condicao", String.class));
        return entity;
    }
}
