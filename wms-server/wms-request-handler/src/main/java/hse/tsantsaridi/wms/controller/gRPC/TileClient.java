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

public class TileClient {
    private static final Logger logger = LoggerFactory.getLogger(TileClient.class);
    /**
     * Отправляет gRPC-запрос на сервер с данными, получает список тайлов
     *
     * @param tileGroup Информация о ноде и список tile_id, которые нужно загрузить
     * @param gmr       Запрос пользователя
     * @return Ответ от сервера с изображением тайлов
     */
    public TileResponse getTiles(TileGroup tileGroup, GetMapRequest gmr) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(tileGroup.getNodeHost(), tileGroup.getNodePort())
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

        try {
            return stub.getTiles(request);
        } catch (StatusRuntimeException e) {
            logger.error("Ошибка gRPC запроса: {}", e.getStatus(), e);
            throw new RuntimeException("gRPC запрос завершился с ошибкой", e);
        } finally {
            channel.shutdownNow();
        }
    }
}
