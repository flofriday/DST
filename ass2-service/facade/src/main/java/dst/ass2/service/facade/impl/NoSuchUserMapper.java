package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.NoSuchUserException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoSuchUserMapper implements ExceptionMapper<NoSuchUserException> {
    @Override
    public Response toResponse(NoSuchUserException exception) {
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity("No such user.")
                .build();
    }
}
