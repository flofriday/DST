package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IRiderDAO;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static dst.ass1.jpa.tests.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ass1_2_2Test extends Ass1_TestBase {

  private IRiderDAO riderDAO;

  @Before
  public void setup() {
    riderDAO = daoFactory.createRiderDAO();
    }

  @Test
  public void findRidersByCurrencyValueAndCurrency_forCurrencyOneAndAmountZero_returnsTwo() {
    var riders = riderDAO.findRidersByCurrencyValueAndCurrency(BigDecimal.ZERO, CURRENCY_1);
    assertEquals(2, riders.size());
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider1Id)));
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider2Id)));
    }

  @Test
  public void findRidersByCurrencyValueAndCurrency_forCurrencyTwoAndAmountZero_returnsThree() {
    var riders = riderDAO.findRidersByCurrencyValueAndCurrency(BigDecimal.ZERO, CURRENCY_2);
    assertEquals(3, riders.size());
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider1Id)));
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider2Id)));
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider3Id)));
    }

  @Test
  public void findRidersByCurrencyValueAndCurrency_forCurrencyTwoAndAmountTwentyOne_returnsTwo() {
    var riders = riderDAO.findRidersByCurrencyValueAndCurrency(BigDecimal.valueOf(21), CURRENCY_2);

    assertEquals(2, riders.size());
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider1Id)));
    assertTrue(riders.stream().anyMatch(rider -> rider.getId().equals(testData.rider3Id)));
    }

  @Test
  public void findRidersByCurrencyValueAndCurrency_forCurrencyThreeAndAmountZero_returnsOne() {
    var riders = riderDAO.findRidersByCurrencyValueAndCurrency(BigDecimal.ZERO, CURRENCY_3);
    assertEquals(1, riders.size());
    assertEquals(testData.rider3Id, riders.get(0).getId());
    }
}
