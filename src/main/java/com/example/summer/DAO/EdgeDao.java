package com.example.summer.DAO;

import com.example.summer.database.EntityManager;
import com.example.summer.model.Novel;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;

import java.util.UUID;

public class EdgeDao {
    EntityManager entityManager;

    public EdgeDao() {
        this.entityManager = new EntityManager();
    }

    public void save(String parentId, String childId) {
        entityManager.execute(
                        "declare $parentId as Utf8;" +
                        "declare $id as Utf8;" +
                        "declare $childId as Utf8;" +
                        "insert into Edges(id, idParent, idChild) values ($id, $parentId, $childId)",
                Params.of("$id", PrimitiveValue.utf8(UUID.randomUUID().toString()),
                        "$parentId", PrimitiveValue.utf8(parentId),
                        "$childId", PrimitiveValue.utf8(childId))
        );
    }
}
