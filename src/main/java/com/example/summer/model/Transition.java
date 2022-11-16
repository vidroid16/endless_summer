package com.example.summer.model;

import com.yandex.ydb.table.result.ResultSetReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transition {
    String id;
    String edgeId;
    String description;

    public Transition(String edgeId, String description) {
        this.id = UUID.randomUUID().toString();
        this.edgeId = edgeId;
        this.description = description;
    }

    public static Transition fromResultSet(ResultSetReader resultSet){
        var id = resultSet.getColumn("id").getUtf8();
        var edgeId = resultSet.getColumn("idEdge").getUtf8();
        var description = resultSet.getColumn("description").getUtf8();
        return new Transition(id,edgeId,description);
    }
}
