package com.datasus.bd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MunicipioSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id_municipio", table, columnPrefix + "_id_municipio"));
        columns.add(Column.aliased("municipio", table, columnPrefix + "_municipio"));

        columns.add(Column.aliased("fk_id_uf_id_uf", table, columnPrefix + "_fk_id_uf_id_uf"));
        return columns;
    }
}
