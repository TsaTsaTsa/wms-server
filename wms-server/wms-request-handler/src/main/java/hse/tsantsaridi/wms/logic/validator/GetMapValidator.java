package hse.tsantsaridi.wms.logic.validator;

import hse.tsantsaridi.wms.models.request.GetMapRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GetMapValidator {
    // Получаем из окружения или берём дефолт по ТЗ
    private static final String SUPPORTED_VERSION =
            System.getenv().getOrDefault("WMS_VERSION", "1.3.0");
    private static final String SUPPORTED_OPERATION =
            System.getenv().getOrDefault("WMS_OPERATION", "GetMap");

    // СПИСОК ФОРМАТОВ — разделённые запятыми в env WMS_FORMATS
    private static final Set<String> SUPPORTED_FORMATS;
    // СПИСОК CRS — разделённые запятыми в env WMS_CRS
    private static final Set<String> SUPPORTED_CRS;

    static {
        String fmtEnv = System.getenv().getOrDefault("WMS_FORMATS", "png");
        SUPPORTED_FORMATS = Arrays.stream(fmtEnv.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        String crsEnv = System.getenv()
                .getOrDefault("WMS_CRS", "EPSG:4326,EPSG:3857,EPSG:102022");
        SUPPORTED_CRS = Arrays.stream(crsEnv.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
    public void validate(GetMapRequest req) {
        // VERSION
        if (!SUPPORTED_VERSION.equals(req.getVersion())) {
            throw new IllegalArgumentException("Unsupported VERSION: " + req.getVersion() + req);
        }

        // REQUEST
        if (!SUPPORTED_OPERATION.equals(req.getRequest())) {
            throw new IllegalArgumentException("Unsupported REQUEST: " + req.getRequest());
        }

        // LAYERS
        List<String> layers = req.getLayers();
        if (layers == null || layers.isEmpty()) {
            throw new IllegalArgumentException("LAYERS must be specified and non-empty");
        }

        // STYLES
        List<String> styles = req.getStyles();
        if (styles == null || styles.size() != layers.size()) {
            throw new IllegalArgumentException("STYLES count must match LAYERS count");
        }

        // CRS
        String crs = req.getCrs();
        if (!SUPPORTED_CRS.contains(crs.toUpperCase())) {
            throw new IllegalArgumentException("Unsupported CRS: " + crs);
        }

        // BBOX: min < max
        double minx = req.getBbox().getMinX();
        double miny = req.getBbox().getMinY();
        double maxx = req.getBbox().getMaxX();
        double maxy = req.getBbox().getMaxY();
        if (!(minx < maxx && miny < maxy)) {
            throw new IllegalArgumentException(
                    String.format("Invalid BBOX: [%f,%f,%f,%f]", minx, miny, maxx, maxy));
        }

        // WIDTH, HEIGHT
        if (req.getWidth() <= 0) {
            throw new IllegalArgumentException("WIDTH must be > 0");
        }
        if (req.getHeight() <= 0) {
            throw new IllegalArgumentException("HEIGHT must be > 0");
        }

        // FORMAT
        String fmt = req.getFormat();
        if (!SUPPORTED_FORMATS.contains(fmt.toLowerCase())) {
            throw new IllegalArgumentException("Invalid FORMAT: " + fmt);
        }

        // TRANSPARENT (TRUE/FALSE)
        String tr = req.getTransparent();
        if (tr != null && !(tr.equalsIgnoreCase("TRUE") || tr.equalsIgnoreCase("FALSE"))) {
            throw new IllegalArgumentException("TRANSPARENT must be TRUE or FALSE");
        }
    }
}
