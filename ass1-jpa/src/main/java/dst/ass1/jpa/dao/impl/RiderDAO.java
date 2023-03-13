package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Rider;

import javax.persistence.EntityManager;
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
        // FIXME: Implement
        return null;
    }

    @Override
    public List<IRider> findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(Date start, Date end) {
        // FIXME: Implement
        return null;
    }

    @Override
    public IRider findByEmail(String email) {
        // FIXME: Implement
        return null;
    }
}
