package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class VacinaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id_vacina", table, columnPrefix + "_id_vacina"));
        columns.add(Column.aliased("fabricante", table, columnPrefix + "_fabricante"));
        columns.add(Column.aliased("nome_vacina", table, columnPrefix + "_nome_vacina"));

        return columns;
    }
}
