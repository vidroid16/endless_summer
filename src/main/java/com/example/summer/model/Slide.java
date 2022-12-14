package com.example.summer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.yandex.ydb.table.result.ResultSetReader;
import lombok.Data;

import java.util.UUID;

@Data
public class Slide {
    private String id;
    private String novelId;
    private String description;
    private String photo;

    public Slide(String novelId, String description) {
        this.id = UUID.randomUUID().toString();
        this.novelId = novelId;
        this.description = description;
    }

    public Slide(String id, String novelId, String description, String photo) {
        this.id = id;
        this.novelId = novelId;
        this.description = description;
        this.photo = photo;
    }

    public static Slide fromResultSet(ResultSetReader resultSet){
        var id = resultSet.getColumn("id").getUtf8();
        var novelId = resultSet.getColumn("novelId").getUtf8();
        var description = resultSet.getColumn("description").getUtf8();
        var photo = resultSet.getColumn("photo").getUtf8();
        return new Slide(id,novelId,description,photo);
    }
}
