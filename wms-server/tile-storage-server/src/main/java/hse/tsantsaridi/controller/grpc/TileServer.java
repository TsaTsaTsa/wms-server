package hse.tsantsaridi.controller.grpc;

import com.google.protobuf.ByteString;
import hse.tsantsaridi.logic.geotiff.GetTilesManager;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import wms.TileServiceGrpc;
import wms.TileServiceOuterClass;
import wms.TileServiceOuterClass.TileResponse;

import java.io.IOException;

public class TileServer extends TileServiceGrpc.TileServiceImplBase {

    @Override
    public void getTiles(TileServiceOuterClass.GetTilesRequest request, StreamObserver<TileServiceOuterClass.TileResponse> responseObserver) {
        try {
            ByteString imageData = GetTilesManager.getTileData(request);

            TileResponse response = TileResponse.newBuilder()
                    .setImage(imageData)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IOException e) {
            // Ошибка при чтении/записи, сообщаем об этом клиенту со статусом INTERNAL
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error while retrieving tiles: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            // Любая иная непредвиденная ошибка
            responseObserver.onError(
                    Status.UNKNOWN
                            .withDescription("Unexpected error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}
