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
        entity.setIdMunicipio(converter.fromRow(row, prefix + "_id_municipio", Long.class));
        entity.setMunicipio(converter.fromRow(row, prefix + "_municipio", String.class));
        entity.setFkIdUfId(converter.fromRow(row, prefix + "_fk_id_uf_id_uf", Long.class));
        return entity;
    }
}
