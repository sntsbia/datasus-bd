package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Municipio;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Municipio}, with proper type conversions.
 */
@Service
public class MunicipioRowMapper implements BiFunction<Row, String, Municipio> {

    private final ColumnConverter converter;

    public MunicipioRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Municipio} stored in the database.
     */
    @Override
    public Municipio apply(Row row, String prefix) {
        Municipio entity = new Municipio();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMunicipioNome(converter.fromRow(row, prefix + "_municipio_nome", String.class));
        entity.setUfId(converter.fromRow(row, prefix + "_uf_id", Long.class));
        return entity;
    }
}
