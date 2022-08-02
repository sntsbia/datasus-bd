package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TesteSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id_teste", table, columnPrefix + "_id_teste"));
        columns.add(Column.aliased("data_teste", table, columnPrefix + "_data_teste"));
        columns.add(Column.aliased("resultado", table, columnPrefix + "_resultado"));

        columns.add(Column.aliased("fk_id_teste_id_pessoa", table, columnPrefix + "_fk_id_teste_id_pessoa"));
        columns.add(Column.aliased("sintomas_id_sintomas", table, columnPrefix + "_sintomas_id_sintomas"));
        return columns;
    }
}
