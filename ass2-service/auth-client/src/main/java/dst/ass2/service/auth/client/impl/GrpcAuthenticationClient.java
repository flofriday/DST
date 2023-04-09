package dst.ass2.service.auth.client.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.*;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcAuthenticationClient implements IAuthenticationClient {

    private ManagedChannel channel;
    private AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    public GrpcAuthenticationClient(AuthenticationClientProperties properties) {
        channel = ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .build();
        blockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        var request = AuthenticationRequest.newBuilder().setEmail(email).setPassword(password).build();
        var response = blockingStub.authenticate(request);
        return response.getToken();
    }

    @Override
    public boolean isTokenValid(String token) {
        var request = TokenValidationRequest.newBuilder().setToken(token).build();
        var response = blockingStub.validateToken(request);
        return response.getValid();
    }

    @Override
    public void close() {
        //channel.shutdown();
    }
}
