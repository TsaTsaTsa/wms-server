package hse.tsantsaridi.wms.models;

import java.util.List;

public class TileGroup {
    private String nodeHost;
    private int nodePort;
    private List<String> tilesIds;

    public TileGroup(String nodeHost, int nodePort, List<String> tilesIds) {
        this.nodeHost = nodeHost;
        this.nodePort = nodePort;
        this.tilesIds = tilesIds;
    }

    public String getNodeHost() {
        return nodeHost;
    }

    public int getNodePort() {
        return nodePort;
    }

    public List<String> getTilesIds() {
        return tilesIds;
    }
}
