package dst.ass1.doc.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import dst.ass1.doc.IDocumentRepository;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.Map;

public class DocumentRepository implements IDocumentRepository {
    // FIXME: Why isn't that in the constructor passed with depenency injection?
    private MongoDatabase db = new MongoClient().getDatabase(Constants.MONGO_DB_NAME);

    @Override
    public void insert(ILocation location, Map<String, Object> locationProperties) {
        var coll = db.getCollection(Constants.COLL_LOCATION_DATA);
        var doc = new Document(locationProperties);
        doc.put("location_id", location.getLocationId());
        doc.put("name", location.getName());
        coll.insertOne(doc);
        coll.createIndex(Indexes.ascending("location_id"));
        coll.createIndex(Indexes.geo2dsphere("geo"));
    }
}
