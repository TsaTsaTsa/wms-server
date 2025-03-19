package hse.tsantsaridi;

import hse.tsantsaridi.controller.grpc.TileServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new TileServer())
                 .maxInboundMessageSize(20 * 1024 * 1024)
                .build();

        server.start();
        System.out.println("Server started on port 50051");

        server.awaitTermination();
    }
}