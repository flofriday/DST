package dst.ass2.service.facade.impl;

import dst.ass2.service.auth.client.IAuthenticationClient;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
//@PreMatching
@AuthenticationRequired
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    IAuthenticationClient authClient;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        String token = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        System.out.println("AUTHFILTER, token:" + token);
        if (token == null) throw new NotAuthorizedException("Bearer");

        if (token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length());
        }
        System.out.println("AUTHFILTER, trimmed token:" + token);

        if (!authClient.isTokenValid(token)) {
            throw new NotAuthorizedException("Bearer error=\"invalid_token\"");
        }
    }
}
