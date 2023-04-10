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
    public Response createTrip(@FormParam("riderId") Long riderId, @FormParam("pickupId") Long pickupId, @FormParam("destinationId") Long destinationId) throws EntityNotFoundException {
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
    @POST
    @Path("{id}/stops")
    @Produces("application/json")
    public Response addStop(@PathParam("id") Long tripId, @FormParam("locationId") Long locationId) throws EntityNotFoundException {
        var trip = tripService.find(tripId);
        var wasAdded = tripService.addStop(trip, locationId);
        if (!wasAdded) {
            return Response
                    .status(Response.Status.PRECONDITION_FAILED)
                    .build();
        }
        return Response
                .ok()
                .entity(trip.getFare())
                .build();
    }

    @Override
    @DELETE
    @Path("{id}/stops/{locationId}")
    public Response removeStop(@PathParam("id") Long tripId, @PathParam("locationId") Long locationId) throws EntityNotFoundException {
        var trip = tripService.find(tripId);
        var wasDeleted = tripService.removeStop(trip, locationId);
        if (!wasDeleted) {
            return Response
                    .status(Response.Status.PRECONDITION_FAILED)
                    .build();
        }
        return Response
                .ok()
                .build();
    }

    @Override
    @POST
    @Path("{id}/match")
    @Consumes("application/json")
    public Response match(@PathParam("id") Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        tripService.match(tripId, matchDTO);
        return Response
                .status(Response.Status.CREATED)
                .build();
    }

    @Override
    @POST
    @Path("{id}/complete")
    public Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        tripService.complete(tripId, tripInfoDTO);
        return Response
                .status(Response.Status.CREATED)
                .build();
    }

    @Override
    @PATCH
    @Path("{id}/cancel")
    public Response cancel(@PathParam("id") Long tripId) throws EntityNotFoundException {
        tripService.cancel(tripId);
        return Response.ok().build();
    }
}
