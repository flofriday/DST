package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceRessource implements ITripServiceResource {

    @Inject
    private ITripService tripService;

    @Override
    @POST
    @Path("")
    public Response createTrip(@QueryParam("riderId") Long riderId, @QueryParam("pickupId") Long pickupId, @QueryParam("destinationId") Long destinationId) throws EntityNotFoundException {
        var trip = tripService.create(riderId, pickupId, destinationId);
        return Response
                .status(Response.Status.OK)
                .entity(trip.getId())
                .build();
    }

    @Override
    @PATCH
    @Path("{id}/confirm")
    public Response confirm(@PathParam("id") Long tripId) throws EntityNotFoundException, InvalidTripException {
        tripService.confirm(tripId);
        return Response.ok().build();
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
    @DELETE
    @Path("{id}")
    public Response deleteTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        tripService.delete(tripId);
        return Response
                .status(Response.Status.OK)
                .build();
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
