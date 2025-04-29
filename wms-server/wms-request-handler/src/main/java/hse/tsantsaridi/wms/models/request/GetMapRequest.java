package hse.tsantsaridi.wms.models.request;

import hse.tsantsaridi.wms.models.capabilities.BoundingBox;

import java.util.List;

public class GetMapRequest {
    private String version;  // Версия протокола
    private final String request = "GetMap";  // Имя операции
    private List<String> layers;  // Список слоев
    private List<String> styles;  // Список стилей
    private String crs;  // Координатная система
    private BoundingBox bbox;  // Координаты BBOX
    private int width;  // Ширина изображения
    private int height;  // Высота изображения
    private String format;  // Формат изображения
    private boolean transparent;  // Прозрачность

    public GetMapRequest() {}
    public GetMapRequest(String version, List<String> layers, List<String> styles,
                         String crs, BoundingBox bbox, int width, int height,
                         String format, boolean transparent) {
        this.version = version;
        this.layers = layers;
        this.styles = styles;
        this.crs = crs;
        this.bbox = bbox;
        this.width = width;
        this.height = height;
        this.format = format;
        this.transparent = transparent;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequest() {
        return request;
    }

    public List<String> getLayers() {
        return layers;
    }

    public void setLayers(List<String> layers) {
        this.layers = layers;
    }

    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public BoundingBox getBbox() {
        return bbox;
    }

    public void setBbox(double minX, double minY, double maxX, double maxY) {
        this.bbox = new BoundingBox(minX, minY, maxX, maxY);
    }


}
