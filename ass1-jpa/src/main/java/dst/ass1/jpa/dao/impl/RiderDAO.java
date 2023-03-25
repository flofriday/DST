package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.Rider;
import dst.ass1.jpa.model.impl.Trip;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RiderDAO implements IRiderDAO {

    private EntityManager em;

    public RiderDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public IRider findById(Long id) {
        return em.find(Rider.class, id);
    }

    @Override
    public List<IRider> findAll() {
        return new ArrayList<>(
                em.createQuery("SELECT r FROM Rider r", Rider.class)
                        .getResultList()
        );
    }

    @Override
    public List<IRider> findRidersByCurrencyValueAndCurrency(BigDecimal currencyValue, String currency) {
        /**
         *  === Note on implementation ===
         *  Yes, I could have done the filtering in Java here, but I decided against it as a best practise. Because:
         *  - RDBS often have many cores or machines available and can scale such filtering with sofisticated algorithms
         *  - Even if the the raw performance is the same we would need to send a lot of data over the network which is
         *    always slower than making the calculations close to the source
         *
         *  Filtering the currency here would be way more complex and a lot more network trafic.
         */
        return new ArrayList<>(em.createNamedQuery("riderBySpentAndCurrency", Rider.class)
                .setParameter("currency", currency)
                .setParameter("currencyValue", currencyValue)
                .getResultList());
    }

    @Override
    public List<IRider> findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(Date start, Date end) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Rider.class);
        Root<Rider> rider = cq.from(Rider.class);
        Join<Rider, Trip> trip = rider.join("trips");

        List<Predicate> conditions = new ArrayList<>();
        conditions.add(cb.le(rider.get("avgRating"), 2.0));
        conditions.add(cb.equal(trip.get("state"), TripState.CANCELLED));
        if (start != null) {
            conditions.add(cb.greaterThanOrEqualTo(trip.get("created"), start));
        }
        if (end != null) {
            conditions.add(cb.lessThanOrEqualTo(trip.get("created"), end));
        }

        cq.select(rider)
                .where(
                        conditions.toArray(Predicate[]::new)
                ).groupBy(rider)
                .orderBy(cb.desc(cb.count(trip)));

        return new ArrayList<>(em.createQuery(cq).setMaxResults(3).getResultList());
    }

    @Override
    public IRider findByEmail(String email) {
        return em.createNamedQuery("riderByEmail", Rider.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
