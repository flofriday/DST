package dst.ass1.doc;

import org.bson.Document;

import java.util.List;

public interface IDocumentQuery {

  List<Document> getAverageOpeningHoursOfRestaurants();

  List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon);

  List<Document> findDocumentsByType(String type);
}
