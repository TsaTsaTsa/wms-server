package hse.tsantsaridi.wms.controller.gRPC;

import hse.tsantsaridi.wms.models.TileGroup;
import hse.tsantsaridi.wms.models.request.GetMapRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import wms.TileServiceGrpc;
import wms.TileServiceOuterClass.GetTilesRequest;
import wms.TileServiceOuterClass.TileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class TileClient {
    private static final Logger logger = LoggerFactory.getLogger(TileClient.class);
    private static final String NGINX_HOST = System.getenv("NGINX_GATEWAY_HOST");

    /**
     * Sends a gRPC request to the appropriate shard via Nginx.
     * A unique requestId is generated per call for log correlation.
     *
     * @param tileGroup Node information and list of tile IDs (contains shardId)
     * @param gmr       User's GetMapRequest
     * @return TileResponse containing image tiles
     */
    public TileResponse getTiles(TileGroup tileGroup, GetMapRequest gmr) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        int port = tileGroup.getPort();
        logger.info("[{}] Routing to {} on port {}", requestId, NGINX_HOST, port);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(NGINX_HOST, port)
                .maxInboundMessageSize(20 * 1024 * 1024)
                .usePlaintext()
                .build();
        TileServiceGrpc.TileServiceBlockingStub stub = TileServiceGrpc.newBlockingStub(channel);

        GetTilesRequest request = GetTilesRequest.newBuilder()
                .setBoundingBoxWKT(gmr.getBbox().toWktString())
                .addAllTileIds(tileGroup.getTilesIds())
                .addAllStyles(gmr.getStyles())
                .addAllLayers(gmr.getLayers())
                .setFormat(gmr.getFormat())
                .build();
        logger.debug("[{}] GetTilesRequest: {}", requestId, request);

        try {
            TileResponse response = stub.getTiles(request);
            logger.info("[{}] Received tiles from shard {}", requestId, port);

            return response;
        } catch (StatusRuntimeException e) {
            logger.error("[{}] gRPC request to shard {} failed: {}", requestId, port, e.getStatus(), e);
            throw new RuntimeException("gRPC request failed for shard " + port, e);
        } finally {
            channel.shutdownNow();
            logger.debug("[{}] Shutdown channel to shard {}:{}", requestId, NGINX_HOST, port);
            MDC.remove("requestId");
        }
    }
}
