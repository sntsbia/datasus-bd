package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Toma;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Toma}, with proper type conversions.
 */
@Service
public class TomaRowMapper implements BiFunction<Row, String, Toma> {

    private final ColumnConverter converter;

    public TomaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Toma} stored in the database.
     */
    @Override
    public Toma apply(Row row, String prefix) {
        Toma entity = new Toma();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setData(converter.fromRow(row, prefix + "_data", LocalDate.class));
        entity.setLote(converter.fromRow(row, prefix + "_lote", String.class));
        entity.setDose(converter.fromRow(row, prefix + "_dose", Long.class));
        entity.setVacinaId(converter.fromRow(row, prefix + "_vacina_id", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
