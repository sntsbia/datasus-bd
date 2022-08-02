package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TomaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data", table, columnPrefix + "_data"));
        columns.add(Column.aliased("lote", table, columnPrefix + "_lote"));
        columns.add(Column.aliased("dose", table, columnPrefix + "_dose"));

        columns.add(Column.aliased("fk_id_vacina_id_vacina", table, columnPrefix + "_fk_id_vacina_id_vacina"));
        columns.add(Column.aliased("fk_id_pessoa_id_pessoa", table, columnPrefix + "_fk_id_pessoa_id_pessoa"));
        return columns;
    }
}
