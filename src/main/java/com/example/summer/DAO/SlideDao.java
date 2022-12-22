package com.example.summer.DAO;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.summer.database.EntityManager;
import com.example.summer.model.Novel;
import com.example.summer.model.Slide;
import com.example.summer.model.Transition;
import com.example.summer.util.ThrowingConsumer;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;

import javax.swing.plaf.PanelUI;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlideDao {

    EntityManager entityManager;

    public SlideDao() {
        this.entityManager = new EntityManager();
    }

    public void save(Slide slide) {
        entityManager.execute(
                "declare $id as Utf8;" +
                        "declare $description as Utf8;" +
                        "declare $novelId as Utf8;" +
                        "insert into Slides(id, description, novelId, photo) values ($id, $description, $novelId, null)",
                Params.of("$id", PrimitiveValue.utf8(slide.getId()),
                        "$description", PrimitiveValue.utf8(slide.getDescription()),
                        "$novelId", PrimitiveValue.utf8(slide.getNovelId()))
        );
    }
    public Slide getById(String slideId){
        final Slide[] slides = {null};
        entityManager.execute(
                "declare $slideId as Utf8;" +
                        "select * from Slides where id = $slideId",
                Params.of("$slideId", PrimitiveValue.utf8(slideId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    resultSet.next();
                    slides[0] = Slide.fromResultSet(resultSet);
                }));
        return slides[0];
    }
    public Transition[] getDescriptions(String slideId){
        final Transition[] transDescr = {null};
        entityManager.execute(
                "declare $slideId as Utf8;" +
                        "select * from Slides s" +
                        "inner join Edges e on s.slideId = e.parentId" +
                        "inner join Transitions t on e.id = t.edgeId" +
                        "inner  where s.id = $slideId",
                Params.of("$slideId", PrimitiveValue.utf8(slideId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    int i = 0;
                    while (resultSet.next()){
                        transDescr[i] = Transition.fromResultSet(resultSet);
                        i++;
                    }
                }));
        return transDescr;
    }
    public List<Transition> getTransitions(String slideId){
        final List<Transition> transDescr = new ArrayList<>();
        entityManager.execute(
                "declare $slideId as Utf8;" +
                        "select t.id as id,t.description as description,t.idEdge as idEdge, s.id as idSlide from Transitions t " +
                        "inner join Edges e on e.id = t.idEdge " +
                        "inner join Slides s on s.id = e.idParent " +
                        "where s.id = $slideId",
                Params.of("$slideId", PrimitiveValue.utf8(slideId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    while (resultSet.next()){
                        transDescr.add(Transition.fromResultSet(resultSet));
                    }
                }));
        return transDescr;
    }
    public List<Transition> getTransitionsByEdgeId(String edgeId){
        final List<Transition> transDescr = new ArrayList<>();
        entityManager.execute(
                "declare $edgeId as Utf8;" +
                        "select t.id as id,t.description as description,t.idEdge as idEdge, s.id as idSlide from Transitions t " +
                        "inner join Edges e on e.id = t.idEdge " +
                        "inner join Slides s on s.id = e.idParent " +
                        "where e.id = $edgeId",
                Params.of("$edgeId", PrimitiveValue.utf8(edgeId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    while (resultSet.next()){
                        transDescr.add(Transition.fromResultSet(resultSet));
                    }
                }));
        return transDescr;
    }
    public String getNextSlideId(String transitionId){
        final String[] ids = {null};
        entityManager.execute(
                "declare $transitionId as Utf8;" +
                        "select s.id as id from Transitions t " +
                        "inner join Edges e on e.id = t.idEdge " +
                        "inner join Slides s on s.id = e.idChild " +
                        "where t.id = $transitionId",
                Params.of("$transitionId", PrimitiveValue.utf8(transitionId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    resultSet.next();
                    ids[0] = resultSet.getColumn("id").getUtf8();
                }));
        return ids[0];
    }

    public void saveTransition(Transition transition){
        entityManager.execute(
                "declare $id as Utf8;" +
                        "declare $description as Utf8;" +
                        "declare $edgeId as Utf8;" +
                        "insert into Transitions(id, description, idEdge) values ($id, $description, $edgeId)",
                Params.of("$id", PrimitiveValue.utf8(transition.getId()),
                        "$description", PrimitiveValue.utf8(transition.getDescription()),
                        "$edgeId", PrimitiveValue.utf8(transition.getEdgeId()))
        );
    }
    public Slide getNextSlide(String transitionId){
        final Slide[] slides = {null};
        entityManager.execute(
                "declare $transitionId as Utf8;" +
                        "select * from Slides " +
                        "inner join Edges on where id = $slideId",
                Params.of("$transitionId", PrimitiveValue.utf8(transitionId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    resultSet.next();
                    slides[0] = Slide.fromResultSet(resultSet);
                }));
        return slides[0];
    }
    public List<Slide> getAllSlideByNovelId(String novelId){
        final List<Slide> slides = new ArrayList<>();
        entityManager.execute(
                "declare $novelId as Utf8;" +
                        "select s.id as id, s.photo as photo, s.description as description, s.novelId as novelId from Slides s " +
                        "where s.novelId = $novelId",
                Params.of("$novelId", PrimitiveValue.utf8(novelId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    while (resultSet.next()){
                        slides.add(Slide.fromResultSet(resultSet));
                    }
                }));
        return slides;
    }
}
