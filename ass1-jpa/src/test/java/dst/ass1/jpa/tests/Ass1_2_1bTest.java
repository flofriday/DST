package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IMatchDAO;
import org.junit.Before;
import org.junit.Test;

import static dst.ass1.jpa.tests.TestData.*;
import static org.junit.Assert.assertEquals;

public class Ass1_2_1bTest extends Ass1_TestBase {

  private IMatchDAO matchDAO;

  @Before
  public void setUp() throws Exception {
    matchDAO = daoFactory.createMatchDAO();
  }

  @Test
  public void countMatchesByDate_forDateWithOneMatch_returnsOne() throws Exception {
    assertEquals(1L, matchDAO.countMatchesByDate(MATCH_3_CREATED));
  }

  @Test
  public void countMatchesByDate_forDateWithTwoMatches_returnsTwo() throws Exception {
    assertEquals(2L, matchDAO.countMatchesByDate(MATCH_1_CREATED));
  }

  @Test
  public void countMatchesByDate_forDateWithoutMatches_returnsZero() throws Exception {
    assertEquals(0L, matchDAO.countMatchesByDate(createDate(2000, 10, 10, 10, 10)));
  }
}
