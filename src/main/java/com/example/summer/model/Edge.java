package com.example.summer.model;

import com.yandex.ydb.table.result.ResultSetReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Edge {
    String id;
    String idChild;
    String idParent;

    public static Edge fromResultSet(ResultSetReader resultSet){
        var id = resultSet.getColumn("id").getUtf8();
        var idChild = resultSet.getColumn("idChild").getUtf8();
        var idParent = resultSet.getColumn("idParent").getUtf8();
        return new Edge(id,idChild,idParent);
    }
}