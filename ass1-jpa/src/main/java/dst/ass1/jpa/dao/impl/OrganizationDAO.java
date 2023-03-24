package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.impl.Driver;
import dst.ass1.jpa.model.impl.Organization;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDAO implements IOrganizationDAO {
    private EntityManager em;

    public OrganizationDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public IOrganization findById(Long id) {
        return em.find(Organization.class, id);
    }

    @Override
    public List<IOrganization> findAll() {
        return new ArrayList<>(
                em.createQuery("SELECT e FROM Organization e", Organization.class)
                        .getResultList()
        );
    }

    @Override
    public IDriver findTopAndActiveDriverByOrganization(long organizationId) {
        return em.createNamedQuery("activeInMultipleOrganizationsDrivers", Driver.class)
                .setParameter("organizationId", organizationId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
