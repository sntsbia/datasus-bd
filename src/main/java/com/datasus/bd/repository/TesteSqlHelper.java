package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TesteSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data_teste", table, columnPrefix + "_data_teste"));
        columns.add(Column.aliased("resultado", table, columnPrefix + "_resultado"));

        columns.add(Column.aliased("pessoa_id", table, columnPrefix + "_pessoa_id"));
        columns.add(Column.aliased("municipio_id", table, columnPrefix + "_municipio_id"));
        return columns;
    }
}