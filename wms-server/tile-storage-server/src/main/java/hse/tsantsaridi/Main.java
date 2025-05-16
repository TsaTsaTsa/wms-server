package hse.tsantsaridi;

import hse.tsantsaridi.controller.grpc.TileServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Server server = ServerBuilder.forPort(50051)
                    .addService(new TileServer())
                    .maxInboundMessageSize(20 * 1024 * 1024)
                    .build();

            server.start();
            logger.info("Server started on port {}", 50051);

            server.awaitTermination();
        } catch (IOException e) {
            logger.error("Failed to start server due to I/O error", e);
            System.exit(1);
        } catch (InterruptedException e) {
            logger.error("Server interrupted", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (RuntimeException e) {
            logger.error("Unexpected error in server runtime", e);
            System.exit(1);
        }
    }
}
