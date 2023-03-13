package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.impl.Driver;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO implements IDriverDAO {

    private EntityManager em;

    public DriverDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public IDriver findById(Long id) {
        return em.find(Driver.class, id);
    }

    @Override
    public List<IDriver> findAll() {
        return new ArrayList<>(
                em.createQuery("SELECT d FROM Driver d", Driver.class)
                        .getResultList()
        );
    }
}
