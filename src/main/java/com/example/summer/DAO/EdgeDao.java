package com.example.summer.DAO;

import com.example.summer.database.EntityManager;
import com.example.summer.model.Edge;
import com.example.summer.model.Novel;
import com.example.summer.model.Slide;
import com.example.summer.util.ThrowingConsumer;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;

import java.util.ArrayList;
import java.util.List;
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
    public List<Edge> getAllEdgesBySlideId(String slideId){
        final List<Edge> edges = new ArrayList<>();
        entityManager.execute(
                "declare $slideId as Utf8;" +
                        "select e.id as id, e.idChild as idChild, e.idParent as idParent from Edges e " +
                        "where e.idParent = $slideId",
                Params.of("$slideId", PrimitiveValue.utf8(slideId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    while (resultSet.next()){
                        edges.add(Edge.fromResultSet(resultSet));
                    }
                }));
        return edges;
    }
}
