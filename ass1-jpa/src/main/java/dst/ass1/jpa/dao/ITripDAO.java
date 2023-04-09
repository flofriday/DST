package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.ITrip;

import java.util.List;

public interface ITripDAO extends GenericDAO<ITrip> {
    public List<ITrip> findActiveTripsByDriver(Long driverId);
}
