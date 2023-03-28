package dst.ass1.doc.impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

public class DocumentQuery implements IDocumentQuery {

    private final MongoDatabase db;

    public DocumentQuery(MongoDatabase db) {
        this.db = db;
    }

    /*
    {"_id": {"$oid": "642333865f05715fb5269941"}, "name": "McDonald's", "openHour": 6, "closingHour": 20, "hours": 14}
    {"_id": {"$oid": "642333865f05715fb5269942"}, "name": "McDonald's", "openHour": 0, "closingHour": 24, "hours": 24}
    {"_id": {"$oid": "642333865f05715fb5269943"}, "name": "McDonald's", "openHour": 10, "closingHour": 20, "hours": 10}
    {"_id": {"$oid": "642333865f05715fb5269944"}, "name": "McDonald's", "openHour": 8, "closingHour": 22, "hours": 14}
    {"_id": {"$oid": "642333865f05715fb526994e"}, "name": "Burger King", "openHour": 10, "closingHour": 24, "hours": 14}
     */
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
                        group("$name", Accumulators.avg("averageOpeningHours", "$hours"))
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
                .projection(fields(include("location_id"), exclude("_id")))
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
