package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IOrganizationDAO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ass1_2_1cTest extends Ass1_TestBase {

  private IOrganizationDAO organizationDao;

  @Before
  public void setUp() throws Exception {
    organizationDao = daoFactory.createOrganizationDAO();
  }

  @Test
  public void findTopAndActiveDriver_forOrganizationOne_returnsDriverOne() throws Exception {
    var driver = organizationDao.findTopAndActiveDriverByOrganization(testData.organization1Id);
    assertEquals(testData.driver1Id, driver.getId());
  }

  @Test
  public void findTopAndActiveDriver_forOrganizationTwo_returnsDriverTwo() throws Exception {
    var driver = organizationDao.findTopAndActiveDriverByOrganization(testData.organization2Id);
    assertEquals(testData.driver2Id, driver.getId());
  }

  @Test
  public void findTopAndActiveDriver_forOrganizationThree_returnsDriverOne() throws Exception {
    var driver = organizationDao.findTopAndActiveDriverByOrganization(testData.organization3Id);
    assertEquals(testData.driver1Id, driver.getId());
  }

  @Test
  public void findTopAndActiveDriver_forOrganizationFour_returnsDriverThree() throws Exception {
    var driver = organizationDao.findTopAndActiveDriverByOrganization(testData.organization4Id);
    assertEquals(testData.driver3Id, driver.getId());
  }

  @Test
  public void findTopAndActiveDriver_forOrganizationWithoutDriverWithMatches_returnsNull()
      throws Exception {
    var driver = organizationDao.findTopAndActiveDriverByOrganization(testData.organization5Id);
    assertNull(driver);
  }

  @Test
  public void findTopAndActiveDriver_forNonExistentOrganization_returnsNull() throws Exception {
    var driver = organizationDao.findTopAndActiveDriverByOrganization(-1L);
    assertNull(driver);
  }
}
