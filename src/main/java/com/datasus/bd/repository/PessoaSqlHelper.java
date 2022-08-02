package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PessoaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id_pessoa", table, columnPrefix + "_id_pessoa"));
        columns.add(Column.aliased("sexo", table, columnPrefix + "_sexo"));
        columns.add(Column.aliased("idade", table, columnPrefix + "_idade"));

        columns.add(Column.aliased("condicao_id_condicao", table, columnPrefix + "_condicao_id_condicao"));
        return columns;
    }
}
