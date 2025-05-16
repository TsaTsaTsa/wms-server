package hse.tsantsaridi.wms.logic.getMap;

import hse.tsantsaridi.wms.controller.gRPC.TileClient;
import hse.tsantsaridi.wms.models.TileGroup;
import hse.tsantsaridi.wms.models.request.GetMapRequest;
import hse.tsantsaridi.wms.logic.validator.GetMapValidator;
import hse.tsantsaridi.wms.repository.TileRepository;
import wms.TileServiceOuterClass.TileResponse;

import java.util.ArrayList;
import java.util.List;

public class GetMapManager {
    private final TileRepository tileRepo = new TileRepository();
    private final TileClient tileClient = new TileClient();

    public List<TileResponse> getResponse(String uri) {
        GetMapRequest gmr = GetMapRequestParser.parse(uri);
        new GetMapValidator().validate(gmr);

        List<TileGroup> tileGroups = tileRepo.findTilesByBBox(gmr.getBbox());
        joinTiles(tileGroups);
        return sendGrpc(tileGroups, gmr);
    }

    private List<TileResponse> sendGrpc(List<TileGroup> tileGroups, GetMapRequest gmr) {
        List<TileResponse> tileResponses = new ArrayList<>();
        for (TileGroup tileGroup : tileGroups) {
            tileResponses.add(tileClient.getTiles(tileGroup, gmr));
        }

        return tileResponses;
    }

    private void joinTiles(List<TileGroup> tileGroups) {

    }
}
