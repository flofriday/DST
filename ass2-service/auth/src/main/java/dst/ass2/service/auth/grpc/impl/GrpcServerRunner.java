package dst.ass2.service.auth.grpc.impl;

import dst.ass2.service.auth.grpc.GrpcServerProperties;
import dst.ass2.service.auth.grpc.IGrpcServerRunner;
import dst.ass2.service.auth.impl.AuthenticationServer;
import dst.ass2.service.auth.impl.CachingAuthenticationService;
import io.grpc.ServerBuilder;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;

@ManagedBean
public class GrpcServerRunner implements IGrpcServerRunner {
    @Inject
    GrpcServerProperties grpcServerProperties;

    @Override
    public void run() throws IOException {
        var serverBuilder = ServerBuilder.forPort(grpcServerProperties.getPort());
        serverBuilder
                .addService(new AuthenticationServer())
                .build()
                .start();
    }
}
