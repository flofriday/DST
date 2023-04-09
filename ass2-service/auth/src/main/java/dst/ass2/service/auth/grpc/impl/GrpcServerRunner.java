package dst.ass2.service.auth.grpc.impl;

import dst.ass2.service.auth.grpc.GrpcServerProperties;
import dst.ass2.service.auth.grpc.IGrpcServerRunner;
import dst.ass2.service.auth.impl.AuthenticationServer;
import io.grpc.ServerBuilder;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;

@ManagedBean
public class GrpcServerRunner implements IGrpcServerRunner {
    @Inject
    GrpcServerProperties grpcServerProperties;

    // NOTE: You need to inject this class because if you create it with new, it's @Inject fields won't be
    // initialized resulting in ugly NullPointerExceptions.
    @Inject
    AuthenticationServer authenticationServer;

    @Override
    public void run() throws IOException {
        var serverBuilder = ServerBuilder.forPort(grpcServerProperties.getPort());
        var server = serverBuilder
                .addService(authenticationServer)
                .build();
        server.start();
//        try {
//            server.awaitTermination();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}
