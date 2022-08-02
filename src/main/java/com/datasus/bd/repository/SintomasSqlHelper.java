package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class SintomasSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id_sintomas", table, columnPrefix + "_id_sintomas"));
        columns.add(Column.aliased("sintomas", table, columnPrefix + "_sintomas"));

        return columns;
    }
}
