package dst.ass2.service.facade.impl;

import com.fasterxml.jackson.core.util.JacksonFeature;
import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/trips")
public class TripServiceResourceFacade implements ITripServiceResource {

    private ITripServiceResource tripServiceResource;

    @Inject
    public TripServiceResourceFacade(URI tripServiceURI) {
        var configuration = new ResourceConfig()
                .packages("dst.ass2")
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
                .register(JacksonFeature.class);

        var client = ClientBuilder.newClient(configuration);
        var webTarget = client.target(tripServiceURI);
        this.tripServiceResource = WebResourceFactory.newResource(ITripServiceResource.class, webTarget);
    }


    @Override
    @POST
    @Path("")
    public Response createTrip(@FormParam("riderId") Long riderId, @FormParam("pickupId") Long pickupId, @FormParam("destinationId") Long destinationId) throws EntityNotFoundException {
        return tripServiceResource.createTrip(riderId, pickupId, destinationId);
    }

    @Override
    @PATCH
    @Path("{id}/confirm")
    public Response confirm(@PathParam("id") Long tripId) throws EntityNotFoundException, InvalidTripException {
        return tripServiceResource.confirm(tripId);
    }

    @Override
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        return tripServiceResource.getTrip(tripId);
    }


    @Override
    @DELETE
    @Path("{id}")
    public Response deleteTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        return tripServiceResource.deleteTrip(tripId);
    }

    @Override
    @POST
    @Path("{id}/stops")
    @Produces("application/json")
    public Response addStop(@PathParam("id") Long tripId, @FormParam("locationId") Long locationId) throws EntityNotFoundException {
        return tripServiceResource.addStop(tripId, locationId);
    }

    @Override
    @DELETE
    @Path("{id}/stops/{locationId}")
    public Response removeStop(@PathParam("id") Long tripId, @PathParam("locationId") Long locationId) throws EntityNotFoundException {
        return tripServiceResource.removeStop(tripId, locationId);
    }

    @Override
    @POST
    @Path("{id}/match")
    @Consumes("application/json")
    public Response match(@PathParam("id") Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        return tripServiceResource.match(tripId, matchDTO);
    }

    @Override
    @POST
    @Path("{id}/complete")
    @Consumes("application/json")
    public Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        return tripServiceResource.complete(tripId, tripInfoDTO);
    }

    @Override
    @PATCH
    @Path("{id}/cancel")
    public Response cancel(@PathParam("id") Long tripId) throws EntityNotFoundException {
        return tripServiceResource.cancel(tripId);
    }
}
