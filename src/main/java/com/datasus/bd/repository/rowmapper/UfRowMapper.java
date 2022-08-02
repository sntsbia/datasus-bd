package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Uf;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Uf}, with proper type conversions.
 */
@Service
public class UfRowMapper implements BiFunction<Row, String, Uf> {

    private final ColumnConverter converter;

    public UfRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Uf} stored in the database.
     */
    @Override
    public Uf apply(Row row, String prefix) {
        Uf entity = new Uf();
        entity.setIdUf(converter.fromRow(row, prefix + "_id_uf", Long.class));
        entity.setCodigoIbge(converter.fromRow(row, prefix + "_codigo_ibge", Long.class));
        entity.setEstado(converter.fromRow(row, prefix + "_estado", String.class));
        entity.setBandeiraContentType(converter.fromRow(row, prefix + "_bandeira_content_type", String.class));
        entity.setBandeira(converter.fromRow(row, prefix + "_bandeira", byte[].class));
        return entity;
    }
}
