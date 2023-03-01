package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IMatch;

import java.util.Date;

public interface IMatchDAO extends GenericDAO<IMatch> {

  /**
   * This method counts all matches for a given date
   * @param date the date where the matches should be counted
   * @return the number of matches for the given date
   */
  long countMatchesByDate(Date date);
}
