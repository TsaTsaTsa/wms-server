package hse.tsantsaridi.wms.models.capabilities;

import java.util.List;

public class ServiceMetadata {
    private String name;
    private String title;
    private Integer maxWidth;
    private Integer maxHeight;
    private Capability capability;
    private List<Layer> layers;

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public void setCapability(Capability capability) {
        this.capability = capability;
    }
}
