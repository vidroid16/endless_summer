package com.example.summer.DAO;

import com.example.summer.database.EntityManager;
import com.example.summer.model.Novel;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;

public class NovelDao{

    EntityManager entityManager;

    public NovelDao() {
        this.entityManager = new EntityManager();
    }

    public void save(Novel novel) {
        entityManager.execute(
                "declare $id as Utf8;" +
                        "declare $name as Utf8;" +
                        "declare $userId as Utf8;" +
                        "insert into Novels(id, name, userId) values ($id, $name, $userId)",
                Params.of("$id", PrimitiveValue.utf8(novel.getId()),
                        "$name", PrimitiveValue.utf8(novel.getName()),
                        "$userId", PrimitiveValue.utf8(novel.getUserId()))
        );
    }
}
