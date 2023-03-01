package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.MongoService;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Ass1_4_3_02Test {

  @ClassRule public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

  @Rule public MongoService mongo = new MongoService(new DocumentTestData());

  @Test
  public void getAverageOpeningHoursOfRestaurants_returnsCorrectAvgOpeningHours() throws Exception {
    List<Document> documentStatistics =
        mongo.getDocumentQuery().getAverageOpeningHoursOfRestaurants();
    assertNotNull(documentStatistics);
    assertEquals(2, documentStatistics.size());

    Map<String, Double> avgOpeningHoursMap =
        documentStatistics.stream()
            .collect(
                Collectors.toMap(d -> d.getString("_id"), d -> d.getDouble("averageOpeningHours")));
    assertThat(
        "expected two aggregation keys",
        avgOpeningHoursMap.keySet(),
        hasItems("Burger King", "McDonald's"));

    assertEquals(14.0, avgOpeningHoursMap.get("Burger King"), 0.1);
    assertEquals(15.5, avgOpeningHoursMap.get("McDonald's"), 0.1);
  }
}
