package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripInfoDAO;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.impl.TripInfo;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class TripInfoDAO implements ITripInfoDAO {
    private EntityManager em;

    public TripInfoDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public ITripInfo findById(Long id) {
        return em.find(TripInfo.class, id);
    }

    @Override
    public List<ITripInfo> findAll() {
        return new ArrayList<>(
                em.createQuery("SELECT e FROM TripInfo e", TripInfo.class)
                        .getResultList()
        );
    }
}
