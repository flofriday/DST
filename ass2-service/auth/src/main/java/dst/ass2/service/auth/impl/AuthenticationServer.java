package dst.ass2.service.auth.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import javax.annotation.ManagedBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ManagedBean
public class AuthenticationServer extends AuthServiceGrpc.AuthServiceImplBase {

    @Inject
    private IAuthenticationService authenticationService;

    @Override
    public void authenticate(AuthenticationRequest request, StreamObserver<AuthenticationResponse> responseObserver) {

        var responseBuilder = AuthenticationResponse.newBuilder();
        try {
            var token = authenticationService.authenticate(request.getEmail(), request.getPassword());
            responseBuilder.setToken(token).setAuthenticated(true);
        } catch (NoSuchUserException e) {
            var status = Status.NOT_FOUND.withCause(e);
            responseObserver.onError(status.asException());
            return;
        } catch (AuthenticationException e) {
            var status = Status.PERMISSION_DENIED.withCause(e);
            responseObserver.onError(status.asException());
            return;
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
