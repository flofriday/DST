package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.dao.impl.TripDAO;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.model.impl.Money;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
@ManagedBean
public class TripService implements ITripService {

    @PersistenceContext(name = "dst")
    private EntityManager em;

    @Inject
    IDAOFactory daoFactory;

    @Inject
    IModelFactory modelFactory;

    @Inject
    IMatchingService matchingService;

    private MoneyDTO moneyToDTO(IMoney money) {
        if (money == null) return null;

        var dto = new MoneyDTO();
        dto.setCurrency(money.getCurrency());
        dto.setValue(money.getCurrencyValue());
        return dto;
    }

    private TripDTO tripToDTO(ITrip trip) {
        if (trip == null) return null;

        var dto = new TripDTO();
        dto.setDestinationId(trip.getDestination().getId());
        //dto.setFare(moneyToDTO(trip.getTripInfo() == null ? null : trip.getTripInfo().getTotal()));
        dto.setId(trip.getId());
        dto.setPickupId(trip.getPickup().getId());
        dto.setRiderId(trip.getRider().getId());
        dto.setStops(trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList()));

        try {
            dto.setFare(matchingService.calculateFare(dto));
        } catch (InvalidTripException e) {
            dto.setFare(null);
        }
        return dto;
    }

    @Override
    @Transactional
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        var model = modelFactory.createTrip();
        var rider = daoFactory.createRiderDAO().findById(riderId);
        var locationDAO = daoFactory.createLocationDAO();
        var pickup = locationDAO.findById(pickupId);
        var destination = locationDAO.findById(destinationId);

        List<Object> deps = Arrays.asList(rider, pickup, destination);
        // FIXME: Better error message?
        if (deps.stream().anyMatch(Objects::isNull))
            throw new EntityNotFoundException("Some dependencies are not available");

        model.setState(TripState.CREATED);
        model.setRider(rider);
        model.setPickup(pickup);
        model.setDestination(destination);
        em.persist(model);

        return tripToDTO(model);
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
        return tripToDTO(daoFactory.createTripDAO().findById(tripId));
    }
}
