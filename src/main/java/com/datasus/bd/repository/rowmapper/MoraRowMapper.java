package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Mora;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Mora}, with proper type conversions.
 */
@Service
public class MoraRowMapper implements BiFunction<Row, String, Mora> {

    private final ColumnConverter converter;

    public MoraRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Mora} stored in the database.
     */
    @Override
    public Mora apply(Row row, String prefix) {
        Mora entity = new Mora();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        entity.setMunicipioId(converter.fromRow(row, prefix + "_municipio_id", Long.class));
        return entity;
    }
}
