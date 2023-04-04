package dst.ass2.service.api.auth.rest;

import javax.ws.rs.core.Response;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;

/**
 * The IAuthenticationResource exposes parts of the {@code IAuthenticationService} as a RESTful interface.
 */
public interface IAuthenticationResource {

    // TODO annotate the class and methods with the correct javax.ws.rs annotations

    Response authenticate(String email, String password)
            throws NoSuchUserException, AuthenticationException;

}
