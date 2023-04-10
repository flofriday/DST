package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceRessource implements ITripServiceResource {

    @Inject
    private ITripService tripService;

    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        return null;
    }

    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        return null;
    }

    @Override
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        var trip = tripService.find(tripId);
        if (trip == null) {
            throw new EntityNotFoundException("No trip with that id exists");
        }

        return Response
                .status(Response.Status.OK)
                .entity(trip)
                .build();
    }

    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        return null;
    }

    @Override
    public Response addStop(Long tripId, Long locationId) throws EntityNotFoundException {
        return null;
    }

    @Override
    public Response removeStop(Long tripId, Long locationId) throws EntityNotFoundException {
        return null;
    }

    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        return null;
    }

    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        return null;
    }

    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        return null;
    }
}
