package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.InvalidTripException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidTripMapper implements ExceptionMapper<InvalidTripException> {

    @Override
    public Response toResponse(InvalidTripException exception) {
        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .entity("Trip is invalid (route cannot be calculated")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
