package com.datasus.bd.repository.rowmapper;

import com.datasus.bd.domain.Vacina;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Vacina}, with proper type conversions.
 */
@Service
public class VacinaRowMapper implements BiFunction<Row, String, Vacina> {

    private final ColumnConverter converter;

    public VacinaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Vacina} stored in the database.
     */
    @Override
    public Vacina apply(Row row, String prefix) {
        Vacina entity = new Vacina();
        entity.setIdVacina(converter.fromRow(row, prefix + "_id_vacina", Long.class));
        entity.setFabricante(converter.fromRow(row, prefix + "_fabricante", String.class));
        entity.setNomeVacina(converter.fromRow(row, prefix + "_nome_vacina", String.class));
        return entity;
    }
}
