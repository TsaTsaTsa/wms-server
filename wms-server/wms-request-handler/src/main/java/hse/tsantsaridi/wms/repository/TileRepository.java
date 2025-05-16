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
    private final String SHARD_PORT = "port";
    private final String SEPARATE_SYMBOL = ",";

    private static final String DB_URL = System.getenv().getOrDefault("TILE_DB_URL", "jdbc:postgresql://localhost:5432/tiles_metadata"); // "jdbc:postgresql://localhost:5432/tiles_metadata";
    private static final String DB_USER = System.getenv().getOrDefault("TILE_DB_USER", "postgres");//"postgres";
    private static final String DB_PASSWORD = System.getenv().getOrDefault("TILE_DB_PASSWORD", "1234");//"1234";
    private static final int POOL_SIZE = Integer.parseInt(System.getenv().getOrDefault("TILE_POOL_SIZE", "10")); // 10;

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
        String sql =
                "SELECT s.port, " +
                        "       STRING_AGG(t.tile_id, ',') AS tile_ids " +
                        "FROM   tiles  t " +
                        "JOIN   shards s ON t.shard_id = s.shard_id " +
                        "WHERE  ST_Intersects(t.bounding_box, ST_MakeEnvelope(?, ?, ?, ?, 4326)) " +
                        "GROUP  BY s.port;";

        List<TileGroup> tileGroups = new ArrayList<>();
        logger.info("findTilesByBBox called with BBOX [{}, {}, {}, {}]", userBoundingBox.getMinX(), userBoundingBox.getMinY(), userBoundingBox.getMaxX(), userBoundingBox.getMaxY());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            logger.debug("Acquired DB connection");

            stmt.setDouble(1, userBoundingBox.getMinX());
            stmt.setDouble(2, userBoundingBox.getMinY());
            stmt.setDouble(3, userBoundingBox.getMaxX());
            stmt.setDouble(4, userBoundingBox.getMaxY());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int port = Integer.parseInt(rs.getString(SHARD_PORT));
                    List<String> tileIds = Arrays.asList(rs.getString(TILE_ID).split(SEPARATE_SYMBOL));
                    tileGroups.add(new TileGroup(port, tileIds));
                }
            } catch (SQLException e) {
                logger.error("SQL Error while executing query: {}", e.getMessage(), e);
                throw new RuntimeException("Error executing SQL query", e);
            } catch (Exception e) {
                logger.error("Unexpected error while finding tiles: {}", e.getMessage(), e);
                throw new RuntimeException("Unexpected error occurred", e);
            }
        } catch (SQLException e) {
            logger.error("Database connection error: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection error", e);
        }

        if (tileGroups.isEmpty()) {
            logger.warn("No tiles found for BBOX [{}, {}, {}, {}]", userBoundingBox.getMinX(), userBoundingBox.getMinY(), userBoundingBox.getMaxX(), userBoundingBox.getMaxY());
        } else {
            logger.info("Get tiles for BBOX [{}, {}, {}, {}] success", userBoundingBox.getMinX(), userBoundingBox.getMinY(), userBoundingBox.getMaxX(), userBoundingBox.getMaxY());
        }
        return tileGroups;
    }
}
