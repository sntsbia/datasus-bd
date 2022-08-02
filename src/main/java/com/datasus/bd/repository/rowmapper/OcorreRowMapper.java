package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Ocorre;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ocorre}, with proper type conversions.
 */
@Service
public class OcorreRowMapper implements BiFunction<Row, String, Ocorre> {

    private final ColumnConverter converter;

    public OcorreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ocorre} stored in the database.
     */
    @Override
    public Ocorre apply(Row row, String prefix) {
        Ocorre entity = new Ocorre();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFkIdTesteId(converter.fromRow(row, prefix + "_fk_id_teste_id_teste", Long.class));
        entity.setFkIdMunicipioId(converter.fromRow(row, prefix + "_fk_id_municipio_id_municipio", Long.class));
        return entity;
    }
}
