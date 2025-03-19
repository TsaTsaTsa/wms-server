package hse.tsantsaridi.wms.models.capabilities;

import java.util.List;

public class Layer {
    private String name;
    private String title;
    private String abstractDescription;
    private List<String> keywordList;
    private List<String> crs;
    private BoundingBox geographicBoundingBox;
    private BoundingBox boundingBox;
    private double minScaleDenominator;
    private double maxScaleDenominator;
    private List<Style> styles;


    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAbstractDescription(String abstractDescription) {
        this.abstractDescription = abstractDescription;
    }

    public void setKeywordList(List<String> keywordList) {
        this.keywordList = keywordList;
    }

    public void setCrs(List<String> crs) {
        this.crs = crs;
    }

    public void setGeographicBoundingBox(BoundingBox geographicBoundingBox) {
        this.geographicBoundingBox = geographicBoundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setMaxScaleDenominator(double maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    public void setMinScaleDenominator(double minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }
}
