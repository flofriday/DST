package dst.ass2.service.trip.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalStateMapper implements ExceptionMapper<IllegalStateException> {

    @Override
    public Response toResponse(IllegalStateException exception) {
        return Response
                .status(422)
                .entity("Trip in a invalid state for the operation.")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
