package hse.tsantsaridi.wms.models;

import java.util.List;

public class TileGroup {
    private int port;
    private List<String> tilesIds;

    public TileGroup(int shardId, List<String> tilesIds) {
        this.port = shardId;
        this.tilesIds = tilesIds;
    }

    public int getPort() {
        return port;
    }

    public List<String> getTilesIds() {
        return tilesIds;
    }
}
