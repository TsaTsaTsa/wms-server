package hse.tsantsaridi.wms.models.capabilities;

import java.util.List;

public class Capability {
    private List<String> operations;
    private List<String> formats;
    private String url;
    private String exception;

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
