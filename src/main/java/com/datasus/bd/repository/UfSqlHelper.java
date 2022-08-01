package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UfSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("codigo_ibge", table, columnPrefix + "_codigo_ibge"));
        columns.add(Column.aliased("estado", table, columnPrefix + "_estado"));
        columns.add(Column.aliased("bandeira", table, columnPrefix + "_bandeira"));
        columns.add(Column.aliased("bandeira_content_type", table, columnPrefix + "_bandeira_content_type"));

        return columns;
    }
}
