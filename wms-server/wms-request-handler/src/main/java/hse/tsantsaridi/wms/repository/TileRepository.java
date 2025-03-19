package hse.tsantsaridi.wms.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hse.tsantsaridi.wms.models.capabilities.BoundingBox;
import hse.tsantsaridi.wms.models.TileGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс-репозиторий для работы с таблицей tiles в базе данных PostGIS.
 * Здесь показаны основные методы:
 * - получение соединения из пула
 * - поиск тайлов по пересечению с BBOX
 */

public class TileRepository {
    private static final Logger logger = LoggerFactory.getLogger(TileRepository.class);

    // Столбцы DB
    private final String TILE_ID = "tile_ids";
    private final String NODE_HOST = "host";
    private final String NODE_PORT = "port";

    private final String SEPARATE_SYMBOL = ",";

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/tiles_metadata";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";
    private static final int POOL_SIZE = 10;

    private static final HikariConfig config = new HikariConfig();

    static {
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(POOL_SIZE);
    }

    private static final HikariDataSource dataSource = new HikariDataSource(config);

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() throws SQLException {
        dataSource.close();
    }

    public List<TileGroup> findTilesByBBox(BoundingBox userBoundingBox) {
        String sql = "SELECT n.host, n.port," +
                "STRING_AGG(t.tile_id, ',') AS tile_ids " +
                "FROM tiles t " +
                "JOIN nodes n ON t.node_id = n.node_id " +
                "WHERE ST_Intersects(t.bounding_box, ST_MakeEnvelope(?, ?, ?, ?, 4326)) " +
                "GROUP BY n.host, n.port;";

        List<TileGroup> tileGroups = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, userBoundingBox.getMinX());
            stmt.setDouble(2, userBoundingBox.getMinY());
            stmt.setDouble(3, userBoundingBox.getMaxX());
            stmt.setDouble(4, userBoundingBox.getMaxY());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nodeHost = rs.getString(NODE_HOST);
                    int nodePort = rs.getInt(NODE_PORT);
                    List<String> tileIds = Arrays.asList(rs.getString(TILE_ID).split(SEPARATE_SYMBOL));
                    tileGroups.add(new TileGroup(nodeHost, nodePort, tileIds));
                }
            } catch (SQLException e) {
                logger.error("SQL Error while executing query: {}", e.getMessage(), e);
                throw new RuntimeException("Error executing SQL query", e);
            } catch (Exception e) {
                logger.error("Unexpected error: {}", e.getMessage(), e);
                throw new RuntimeException("Unexpected error occurred", e);
            }

        } catch (SQLException e) {
            logger.error("Database connection error: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection error", e);
        }

        return tileGroups;
    }
}
