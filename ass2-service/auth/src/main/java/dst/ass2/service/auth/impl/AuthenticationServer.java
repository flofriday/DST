package dst.ass2.service.auth.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.*;
import io.grpc.stub.StreamObserver;

import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class AuthenticationServer extends AuthServiceGrpc.AuthServiceImplBase {

    @Inject
    IAuthenticationService authenticationService;

    @Override
    public void authenticate(AuthenticationRequest request, StreamObserver<AuthenticationResponse> responseObserver) {

        var responseBuilder = AuthenticationResponse.newBuilder();
        try {
            var token = authenticationService.authenticate(request.getEmail(), request.getPassword());
            responseBuilder.setToken(token).setAuthenticated(true);
        } catch (NoSuchUserException e) {
            responseBuilder.setAuthenticated(false);
        } catch (AuthenticationException e) {
            responseBuilder.setAuthenticated(false);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(TokenValidationRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        var valid = authenticationService.isValid(request.getToken());
        var response = TokenValidationResponse
                .newBuilder()
                .setValid(valid)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
