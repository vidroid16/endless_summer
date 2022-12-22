package com.example.summer.DAO;

import com.example.summer.database.EntityManager;
import com.example.summer.model.Novel;
import com.example.summer.model.Slide;
import com.example.summer.util.ThrowingConsumer;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;

import java.util.ArrayList;
import java.util.List;

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

    public List<Novel> getAllNovels(){
        final List<Novel> novels = new ArrayList<>();
        entityManager.execute(
                "declare $id as Utf8;" +
                        "select n.id as id, n.userId as userId, n.name as name, n.startSlideId as startSlideId from Novels n where n.id<>$id;",
                Params.of("$id", PrimitiveValue.utf8("id")),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    while (resultSet.next()){
                        novels.add(Novel.fromResultSet(resultSet));
                    }
                }));
        return novels;
    }
    public void setStartSlide(String novelId, String startSlideId) {
        entityManager.execute(
                "declare $slideId as Utf8;" +
                        "declare $id as Utf8;" +
                        "update Novels set startSlideId = $slideId " +
                        "where id = $id;",
                Params.of("$id", PrimitiveValue.utf8(novelId),
                        "$slideId", PrimitiveValue.utf8(startSlideId))
        );
    }
    public Novel getById(String novelId){
        final Novel[] novels = {null};
        entityManager.execute(
                "declare $novelId as Utf8;" +
                        "select * from Novels where id = $novelId",
                Params.of("$novelId", PrimitiveValue.utf8(novelId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    resultSet.next();
                    novels[0] = Novel.fromResultSet(resultSet);
                }));
        return novels[0];
    }
}
