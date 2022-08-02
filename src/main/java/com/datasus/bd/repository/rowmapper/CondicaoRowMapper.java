package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Condicao;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Condicao}, with proper type conversions.
 */
@Service
public class CondicaoRowMapper implements BiFunction<Row, String, Condicao> {

    private final ColumnConverter converter;

    public CondicaoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Condicao} stored in the database.
     */
    @Override
    public Condicao apply(Row row, String prefix) {
        Condicao entity = new Condicao();
        entity.setIdCondicao(converter.fromRow(row, prefix + "_id_condicao", Long.class));
        entity.setCondicao(converter.fromRow(row, prefix + "_condicao", String.class));
        return entity;
    }
}
