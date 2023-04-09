package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.dao.impl.TripDAO;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.model.impl.Driver;
import dst.ass1.jpa.model.impl.Money;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass1.jpa.model.impl.TripInfo;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
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

    private TripDTO tripToDTO(ITrip trip) {
        if (trip == null) return null;

        var dto = new TripDTO();
        dto.setDestinationId(trip.getDestination().getId());
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
    @Transactional
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {
        var model = daoFactory.createTripDAO().findById(tripId);
        if (model == null) throw new EntityNotFoundException("No such trip exists");
        if (model.getState() != TripState.CREATED) throw new IllegalStateException();

        model.setState(TripState.QUEUED);
        em.persist(model);

        var dto = tripToDTO(model);
        dto.setFare(matchingService.calculateFare(dto));

        matchingService.queueTripForMatching(tripId);
    }

    @Override
    @Transactional
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        var tripDAO = daoFactory.createTripDAO();
        var tripModel = tripDAO.findById(tripId);
        if (tripModel == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("No such trip exists");
        }
        if (tripModel.getState() != TripState.QUEUED || tripModel.getRider() == null) throw new IllegalStateException();

        var driverModel = daoFactory.createDriverDAO().findById(match.getDriverId());
        if (driverModel == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("No such driver exists");
        }


        var vehicleModel = daoFactory.createVehicleDAO().findById(match.getVehicleId());
        if (vehicleModel == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("No such vehicle exists");
        }

        if (!tripDAO.findActiveTripsByDriver(driverModel.getId()).isEmpty()) {
            matchingService.queueTripForMatching(tripId);
            throw new DriverNotAvailableException("The driver is already assigned");
        }

        var moneyModel = modelFactory.createMoney();
        moneyModel.setCurrency(match.getFare().getCurrency());
        moneyModel.setCurrencyValue(match.getFare().getValue());

        var matchModel = modelFactory.createMatch();
        matchModel.setTrip(tripModel);
        matchModel.setDriver(driverModel);
        matchModel.setVehicle(vehicleModel);
        matchModel.setDate(new Date());
        matchModel.setFare(moneyModel);
        em.persist(matchModel);

        tripModel.setState(TripState.MATCHED);
        em.persist(tripModel);
    }

    @Override
    @Transactional
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        var trip = daoFactory.createTripDAO().findById(tripId);
        if (trip == null) throw new EntityNotFoundException("No such trip exists");
        trip.setState(TripState.COMPLETED);

        var tripInfo = new TripInfo();
        tripInfo.setTrip(trip);
        tripInfo.setCompleted(tripInfoDTO.getCompleted());
        tripInfo.setDistance(tripInfoDTO.getDistance());
        var total = new Money();
        total.setCurrency(tripInfoDTO.getFare().getCurrency());
        total.setCurrencyValue(tripInfoDTO.getFare().getValue());
        tripInfo.setTotal(total);
        em.persist(tripInfo);
    }

    @Override
    @Transactional
    public void cancel(Long tripId) throws EntityNotFoundException {
        var model = daoFactory.createTripDAO().findById(tripId);
        if (model == null) throw new EntityNotFoundException("No such trip exists");
        model.setState(TripState.CANCELLED);
        em.persist(model);
    }

    @Override
    @Transactional
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        var model = daoFactory.createTripDAO().findById(trip.getId());
        if (model == null) throw new EntityNotFoundException("No such trip exists");
        if (model.getState() != TripState.CREATED) throw new IllegalStateException();

        var location = daoFactory.createLocationDAO().findById(locationId);
        if (location == null) throw new EntityNotFoundException("No such location exists");

        if (trip.getStops().contains(locationId)) return false;

        model.addStop(location);
        em.persist(model);
        trip.getStops().add(locationId);
        try {
            trip.setFare(matchingService.calculateFare(trip));
        } catch (InvalidTripException e) {
            trip.setFare(null);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        var model = daoFactory.createTripDAO().findById(trip.getId());
        if (model == null) throw new EntityNotFoundException("No such trip exists");
        if (model.getState() != TripState.CREATED) throw new IllegalStateException();

        var location = daoFactory.createLocationDAO().findById(locationId);
        if (location == null) throw new EntityNotFoundException("No such location exists");

        if (!trip.getStops().contains(locationId)) return false;

        model.setStops(model.getStops().stream().filter(l -> l.getId() != locationId).collect(Collectors.toList()));
        em.persist(model);
        trip.getStops().remove(locationId);
        try {
            trip.setFare(matchingService.calculateFare(trip));
        } catch (InvalidTripException e) {
            trip.setFare(null);
        }

        return true;
    }

    @Override
    @Transactional
    public void delete(Long tripId) throws EntityNotFoundException {
        var model = daoFactory.createTripDAO().findById(tripId);
        if (model == null) throw new EntityNotFoundException("No such trip exists");
        em.remove(model);
    }

    @Override
    public TripDTO find(Long tripId) {
        return tripToDTO(daoFactory.createTripDAO().findById(tripId));
    }
}
