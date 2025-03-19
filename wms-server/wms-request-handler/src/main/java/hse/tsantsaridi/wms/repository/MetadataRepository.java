package hse.tsantsaridi.wms.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hse.tsantsaridi.wms.models.capabilities.BoundingBox;
import hse.tsantsaridi.wms.models.capabilities.Layer;
import hse.tsantsaridi.wms.models.capabilities.ServiceMetadata;
import hse.tsantsaridi.wms.models.capabilities.Style;
import hse.tsantsaridi.wms.models.capabilities.Capability;
import com.thoughtworks.xstream.XStream;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetadataRepository {
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final int POOL_SIZE = 10;

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(POOL_SIZE);
        dataSource = new HikariDataSource(config);
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() throws SQLException {
        dataSource.close();
    }

    public List<ServiceMetadata> getServiceMetadataAsXml() throws SQLException {
        String sql = "SELECT * FROM service_metadata";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<ServiceMetadata> serviceMetadataList = new ArrayList<>();
            while (rs.next()) {
                ServiceMetadata metadata = new ServiceMetadata();
                metadata.setName(rs.getString("name"));
                metadata.setTitle(rs.getString("title"));
                metadata.setMaxWidth(rs.getInt("max_width"));
                metadata.setMaxHeight(rs.getInt("max_height"));

                metadata.setCapability(getCapabilityByServiceId(rs.getInt("id")));
                metadata.setLayers(getLayersByServiceId(rs.getInt("id")));
                serviceMetadataList.add(metadata);
            }
            return serviceMetadataList;
        }
    }

    // Метод для получения слоев по service_id
    private List<Layer> getLayersByServiceId(int serviceId) throws SQLException {
        String sql = "SELECT * FROM layers WHERE service_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();

            List<Layer> layers = new ArrayList<>();
            while (rs.next()) {
                Layer layer = new Layer();
                layer.setName(rs.getString("name"));
                layer.setTitle(rs.getString("title"));
                layer.setAbstractDescription(rs.getString("abstract_description"));
                layer.setMinScaleDenominator(rs.getDouble("min_scale_denominator"));
                layer.setMaxScaleDenominator(rs.getDouble("max_scale_denominator"));

                // Получение стилей для слоя
                layer.setStyles(getStylesByLayerId(rs.getInt("id")));

                // Получение ограничивающих прямоугольников
                BoundingBox boundingBox = new BoundingBox(
                        rs.getDouble("bounding_box_min_x"),
                        rs.getDouble("bounding_box_min_y"),
                        rs.getDouble("bounding_box_max_x"),
                        rs.getDouble("bounding_box_max_y"));
                layer.setBoundingBox(boundingBox);

                layers.add(layer);
            }
            return layers;
        }
    }

    // Метод для получения стилей по layer_id
    private List<Style> getStylesByLayerId(int layerId) throws SQLException {
        String sql = "SELECT * FROM styles WHERE layer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, layerId);
            ResultSet rs = stmt.executeQuery();

            List<Style> styles = new ArrayList<>();
            while (rs.next()) {
                Style style = new Style();
                style.setName(rs.getString("name"));
                style.setTitle(rs.getString("title"));
                style.setAbstractDescription(rs.getString("abstract_description"));
                style.setLegendURL(rs.getString("legend_url"));

                styles.add(style);
            }
            return styles;
        }
    }

    // Метод для получения информации о Capability
    private Capability getCapabilityByServiceId(int serviceId) throws SQLException {
        String sql = "SELECT * FROM capability WHERE service_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Capability capability = new Capability();
                capability.setOperations(getOperationsByCapabilityId(rs.getInt("id")));
                capability.setFormats(getFormatsByCapabilityId(rs.getInt("id")));
                capability.setUrl(rs.getString("url"));
                capability.setException(rs.getString("exception"));

                return capability;
            }
            return null;
        }
    }

    // Получаем операции для capability
    private List<String> getOperationsByCapabilityId(int capabilityId) throws SQLException {
        String sql = "SELECT operation_name FROM capability_operations WHERE capability_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, capabilityId);
            ResultSet rs = stmt.executeQuery();

            List<String> operations = new ArrayList<>();
            while (rs.next()) {
                operations.add(rs.getString("operation_name"));
            }
            return operations;
        }
    }

    // Получаем форматы для capability
    private List<String> getFormatsByCapabilityId(int capabilityId) throws SQLException {
        String sql = "SELECT format_name FROM capability_formats WHERE capability_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, capabilityId);
            ResultSet rs = stmt.executeQuery();

            List<String> formats = new ArrayList<>();
            while (rs.next()) {
                formats.add(rs.getString("format_name"));
            }
            return formats;
        }
    }
}
