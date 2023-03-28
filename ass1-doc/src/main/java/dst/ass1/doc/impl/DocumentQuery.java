package dst.ass1.doc.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class DocumentQuery implements IDocumentQuery {

    private MongoDatabase db;

    public DocumentQuery(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public List<Document> getAverageOpeningHoursOfRestaurants() {
        // FIXME: Implement
        return null;
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        // FIXME: Implement
        return null;
    }

    @Override
    public List<Document> findDocumentsByType(String type) {
        return db.getCollection(Constants.COLL_LOCATION_DATA)
                .find(new Document("type", type))
                .into(new ArrayList<>());
    }
}
