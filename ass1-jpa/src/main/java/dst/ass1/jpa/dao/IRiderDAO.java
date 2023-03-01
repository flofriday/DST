package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IRider;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IRiderDAO extends GenericDAO<IRider> {

  /**
   * Finds all Riders that have spent more than a given amount of a specific currency
   *
   * @param currencyValue the amount of the currency spent
   * @param currency the currency used
   * @return a list of riders
   */
  List<IRider> findRidersByCurrencyValueAndCurrency(BigDecimal currencyValue, String currency);

  /**
   * Finds the top three riders that have the most cancelled trips and a rating lower equal to two
   * in a given date range
   *
   * @param start the lower bound of the date range
   * @param end the upper bound of the date range
   * @return a list of maximum three riders
   */
  List<IRider> findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(Date start, Date end);

  /**
   * Returns the rider associated with the given email. Returns null if the email does not exist.
   *
   * @param email the email address
   * @return the rider or null
   */
  IRider findByEmail(String email);
}
