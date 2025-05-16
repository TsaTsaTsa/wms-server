package hse.tsantsaridi.controller.grpc;

import com.google.protobuf.ByteString;
import hse.tsantsaridi.logic.geotiff.GetTilesManager;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import wms.TileServiceGrpc;
import wms.TileServiceOuterClass;
import wms.TileServiceOuterClass.TileResponse;

import java.io.IOException;
import java.util.UUID;

public class TileServer extends TileServiceGrpc.TileServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(TileServer.class);

    @Override
    public void getTiles(TileServiceOuterClass.GetTilesRequest request,
                         StreamObserver<TileServiceOuterClass.TileResponse> responseObserver) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        try {
            logger.info("[{}] getTiles called with {} tile IDs", requestId, request.getTileIdsCount());
            ByteString imageData = GetTilesManager.getTileData(request);

            TileResponse response = TileResponse.newBuilder()
                    .setImage(imageData)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("[{}] getTiles succeeded: returned image of size {} bytes", requestId, imageData.size());
        } catch (IOException e) {
            logger.error("[{}] I/O error in getTiles: {}", requestId, e.getMessage(), e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error while retrieving tiles: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            logger.error("[{}] Unexpected error in getTiles", requestId, e);
            responseObserver.onError(
                    Status.UNKNOWN
                            .withDescription("Unexpected error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } finally {
            MDC.remove("requestId");
        }
    }
}
