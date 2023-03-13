package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.*;

import javax.persistence.EntityManager;

public class DAOFactory implements IDAOFactory {

    private EntityManager em;

    public DAOFactory(EntityManager em) {
        this.em = em;
    }

    @Override
    public IDriverDAO createDriverDAO() {
        return new DriverDAO(em);
    }

    @Override
    public IEmploymentDAO createEmploymentDAO() {
        return new EmploymentDAO(em);
    }

    @Override
    public ILocationDAO createLocationDAO() {
        return new LocationDAO(em);
    }

    @Override
    public IMatchDAO createMatchDAO() {
        return new MatchDAO(em);
    }

    @Override
    public IOrganizationDAO createOrganizationDAO() {
        return new OrganizationDAO(em);
    }

    @Override
    public IRiderDAO createRiderDAO() {
        return new RiderDAO(em);
    }

    @Override
    public ITripDAO createTripDAO() {
        return new TripDAO(em);
    }

    @Override
    public ITripInfoDAO createTripInfoDAO() {
        return new TripInfoDAO(em);
    }

    @Override
    public IVehicleDAO createVehicleDAO() {
        return new VehicleDAO(em);
    }
}
