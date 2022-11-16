package com.example.summer.model;

import com.yandex.ydb.table.result.ResultSetReader;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Novel {
    private String id;
    private String userId;
    private String name;

    public Novel(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.userId = UUID.randomUUID().toString();
    }
    public static Novel fromResultSet(ResultSetReader resultSet){
        var id = resultSet.getColumn("id").getUtf8();
        var userId = resultSet.getColumn("userId").getUtf8();
        var name = resultSet.getColumn("name").getUtf8();
        return new Novel(id,userId,name);
    }
}
