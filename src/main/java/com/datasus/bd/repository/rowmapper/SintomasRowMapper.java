package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Sintomas;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Sintomas}, with proper type conversions.
 */
@Service
public class SintomasRowMapper implements BiFunction<Row, String, Sintomas> {

    private final ColumnConverter converter;

    public SintomasRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Sintomas} stored in the database.
     */
    @Override
    public Sintomas apply(Row row, String prefix) {
        Sintomas entity = new Sintomas();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricaoSintoma(converter.fromRow(row, prefix + "_descricao_sintoma", String.class));
        return entity;
    }
}
