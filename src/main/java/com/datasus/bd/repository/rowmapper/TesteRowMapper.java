package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Teste;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Teste}, with proper type conversions.
 */
@Service
public class TesteRowMapper implements BiFunction<Row, String, Teste> {

    private final ColumnConverter converter;

    public TesteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Teste} stored in the database.
     */
    @Override
    public Teste apply(Row row, String prefix) {
        Teste entity = new Teste();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDataTeste(converter.fromRow(row, prefix + "_data_teste", LocalDate.class));
        entity.setResultado(converter.fromRow(row, prefix + "_resultado", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        entity.setMunicipioId(converter.fromRow(row, prefix + "_municipio_id", Long.class));
        return entity;
    }
}
