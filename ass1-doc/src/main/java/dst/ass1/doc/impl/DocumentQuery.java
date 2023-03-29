package dst.ass1.doc.impl;

import com.mongodb.client.MongoDatabase;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

public class DocumentQuery implements IDocumentQuery {

    private final MongoDatabase db;

    public DocumentQuery(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public List<Document> getAverageOpeningHoursOfRestaurants() {
        var res = db.getCollection(Constants.COLL_LOCATION_DATA).aggregate(
                Arrays.asList(
                        match(eq("category", "Restaurant")),
                        project(
                                fields(
                                        include("name", "closingHour", "openHour"),
                                        computed("hours",
                                                new Document("$subtract", Arrays.asList("$closingHour", "$openHour"))
                                        )
                                )
                        ),
                        group("$name", avg("averageOpeningHours", "$hours"))
                )
        ).into(new ArrayList<>());

        res.forEach(doc -> System.out.println(doc.toJson()));
        return res;
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        var res = db.getCollection(Constants.COLL_LOCATION_DATA)
                .find(
                        and(
                                geoWithinPolygon("geo", polygon),
                                regex("name", ".*" + Pattern.quote(name) + ".*")
                        ))
                .projection(
                        fields(
                                include("location_id"),
                                exclude("_id")
                        )
                )
                .into(new ArrayList<>());

        res.forEach(doc -> System.out.println(doc.toJson()));
        return res;
    }

    @Override
    public List<Document> findDocumentsByType(String type) {
        return db.getCollection(Constants.COLL_LOCATION_DATA)
                .find(new Document("type", type))
                .into(new ArrayList<>());
    }
}
