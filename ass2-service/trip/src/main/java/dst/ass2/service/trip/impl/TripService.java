package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.ejb.Singleton;

@Singleton
@ManagedBean
public class TripService implements ITripService {

    //@Inject
    //IDAOFactory daoFactory;

    //@Inject
    //IModelFactory modelFactory;

    @Override
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        // FIXME: Implement
        return null;
    }

    @Override
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {
        // FIXME: Implement

    }

    @Override
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        // FIXME: Implement

    }

    @Override
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        // FIXME: Implement

    }

    @Override
    public void cancel(Long tripId) throws EntityNotFoundException {
        // FIXME: Implement

    }

    @Override
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        // FIXME: Implement
        return false;
    }

    @Override
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        // FIXME: Implement
        return false;
    }

    @Override
    public void delete(Long tripId) throws EntityNotFoundException {
        // FIXME: Implement

    }

    @Override
    public TripDTO find(Long tripId) {
        // FIXME: Implement
        return null;
    }
}
