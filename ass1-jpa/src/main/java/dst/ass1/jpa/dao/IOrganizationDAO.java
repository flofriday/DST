package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IOrganization;

public interface IOrganizationDAO extends GenericDAO<IOrganization> {

  /**
   * Returns the active driver which has covered the greatest distance for a specific organization
   *
   * @param organizationId the id of the organization to search the top driver for
   * @return the top driver of the organization or null if none found
   */
  IDriver findTopAndActiveDriverByOrganization(long organizationId);
}
