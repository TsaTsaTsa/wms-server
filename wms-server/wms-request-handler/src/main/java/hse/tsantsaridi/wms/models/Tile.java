package hse.tsantsaridi.wms.models;

/**
 * Класс, описывающий структуру тайла в базе данных.
 * Соответствует таблице tiles:
 *
 * CREATE TABLE tiles (
 *   id SERIAL PRIMARY KEY,
 *   tile_id VARCHAR(255) UNIQUE,
 *   bounding_box GEOMETRY(Polygon, 4326),
 *   node_id INT
 * );
 */
public class Tile {
    private int id;
    private String tileId;
    private String boundingBoxWKT;
    private int nodeId;

    public Tile(int id, String tileId, String boundingBoxWKT, int nodeId) {
        this.id = id;
        this.tileId = tileId;
        this.boundingBoxWKT = boundingBoxWKT;
        this.nodeId = nodeId;
    }

    public int getId() {
        return id;
    }

    public String getTileId() {
        return tileId;
    }

    public String getBoundingBoxWKT() {
        return boundingBoxWKT;
    }

    public int getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "id=" + id +
                ", tileId='" + tileId + '\'' +
                ", boundingBoxWKT='" + boundingBoxWKT + '\'' +
                ", nodeId=" + nodeId +
                '}';
    }
}

