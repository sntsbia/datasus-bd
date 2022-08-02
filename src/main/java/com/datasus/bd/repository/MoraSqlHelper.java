package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MoraSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));

        columns.add(Column.aliased("fk_id_pessoa_id_pessoa", table, columnPrefix + "_fk_id_pessoa_id_pessoa"));
        columns.add(Column.aliased("fk_id_municipio_id_municipio", table, columnPrefix + "_fk_id_municipio_id_municipio"));
        return columns;
    }
}
