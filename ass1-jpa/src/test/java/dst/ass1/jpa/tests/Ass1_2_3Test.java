package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.TripState;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static dst.ass1.jpa.tests.TestData.createDate;
import static org.junit.Assert.assertEquals;

public class Ass1_2_3Test extends Ass1_TestBase {

  private IRiderDAO riderDAO;
  private ITripDAO tripDAO;

  @Before
  public void setUp() throws Exception {
    riderDAO = daoFactory.createRiderDAO();
    tripDAO = daoFactory.createTripDAO();
  }

  @Test
  public void findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo_returnsThreeRiders() {
    em.getTransaction().begin();
    var rider1 = createNewRiderWithZeroRatingAndEmail("abc1@gmx.at");
    var rider2 = createNewRiderWithZeroRatingAndEmail("abc2@gmx.at");
    var rider3 = createNewRiderWithZeroRatingAndEmail("abc3@gmx.at");
    var rider4 = createNewRiderWithZeroRatingAndEmail("abc4@gmx.at");

    addNCancelledTripsToRider(rider1, 10, new Date());
    addNCancelledTripsToRider(rider2, 15, new Date());
    addNCancelledTripsToRider(rider3, 20, new Date());
    addNCancelledTripsToRider(rider4, 5, new Date());

    var riders = riderDAO.findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(null, null);

    assertEquals(3, riders.size());
    assertEquals(rider3.getId(), riders.get(0).getId());
    assertEquals(rider2.getId(), riders.get(1).getId());
    assertEquals(rider1.getId(), riders.get(2).getId());
  }

  @Test
  public void
      findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo_WithOneRider_returnsOneRider() {
    em.getTransaction().begin();
    var rider1 = createNewRiderWithZeroRatingAndEmail("abc1@gmx.at");
    addNCancelledTripsToRider(rider1, 10, new Date());

    var riders = riderDAO.findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(null, null);

    assertEquals(1, riders.size());
    assertEquals(rider1.getId(), riders.get(0).getId());
  }

  @Test
  public void
      findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo_WithEndDate_returnsOneRider() {
    em.getTransaction().begin();
    var rider1 = createNewRiderWithZeroRatingAndEmail("abc1@gmx.at");
    var rider2 = createNewRiderWithZeroRatingAndEmail("abc2@gmx.at");

    addNCancelledTripsToRider(rider1, 1, createDate(2000, 1, 1, 1, 1));
    addNCancelledTripsToRider(rider2, 1, createDate(2000, 1, 3, 1, 1));

    var riders =
        riderDAO.findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(
            null, createDate(2000, 1, 2, 1, 1));

    assertEquals(1, riders.size());
    assertEquals(rider1.getId(), riders.get(0).getId());
  }

  @Test
  public void
      findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo_WithStartDate_returnsOneRider() {
    em.getTransaction().begin();
    var rider1 = createNewRiderWithZeroRatingAndEmail("abc1@gmx.at");
    var rider2 = createNewRiderWithZeroRatingAndEmail("abc2@gmx.at");

    addNCancelledTripsToRider(rider1, 1, createDate(2000, 1, 1, 1, 1));
    addNCancelledTripsToRider(rider2, 1, createDate(2000, 1, 3, 1, 1));

    var riders =
        riderDAO.findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(
            createDate(2000, 1, 2, 1, 1), null);

    assertEquals(1, riders.size());
    assertEquals(rider2.getId(), riders.get(0).getId());
  }

  @Test
  public void
      findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo_WithStartAndEndDate_returnsOneRider() {
    em.getTransaction().begin();
    var rider1 = createNewRiderWithZeroRatingAndEmail("abc1@gmx.at");
    var rider2 = createNewRiderWithZeroRatingAndEmail("abc2@gmx.at");

    addNCancelledTripsToRider(rider1, 1, createDate(2000, 1, 3, 1, 1));
    addNCancelledTripsToRider(rider2, 1, createDate(2002, 1, 1, 1, 1));

    var riders =
        riderDAO.findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(
            createDate(2000, 1, 2, 1, 1), createDate(2000, 1, 4, 1, 1));

    assertEquals(1, riders.size());
    assertEquals(rider1.getId(), riders.get(0).getId());
  }

  private IRider createNewRiderWithZeroRatingAndEmail(String email) {
    var rider = modelFactory.createRider();
    rider.setEmail(email);
    rider.setAvgRating(0.0);
    rider.setTel("000");
    em.persist(rider);
    return rider;
  }

  private void addNCancelledTripsToRider(IRider rider, int numberOfTrips, Date date) {

    var trip = tripDAO.findById(testData.trip1Id);
    trip.setStops(null);
    trip.setState(TripState.CANCELLED);
    trip.setRider(rider);

    for (int i = 0; i < numberOfTrips; i++) {
      em.detach(trip);
      trip.setId(null);
      em.persist(trip);
      trip.setCreated(date);
      em.persist(trip);
    }
  }
}
