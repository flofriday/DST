package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.AuthenticationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(AuthenticationException exception) {
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity("Authentication failed")
                .build();
    }
}
