package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.DriverNotAvailableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DriverNotAvailableMapper implements ExceptionMapper<DriverNotAvailableException> {

    @Override
    public Response toResponse(DriverNotAvailableException exception) {
        // NOTE: We will use conflict because the ressource (driver) is already used.
        return Response
                .status(Response.Status.CONFLICT)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
