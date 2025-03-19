package hse.tsantsaridi.wms.logic.getMap;

import hse.tsantsaridi.wms.models.request.GetMapRequest;

import java.util.Arrays;
import java.util.List;

public class GetMapRequestParser {

    public static GetMapRequest parse(String queryString) {
        GetMapRequest request = new GetMapRequest();

        String[] params = queryString.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            String key = keyValue[0];
            String value = keyValue.length == 2 ? keyValue[1] : "";

            switch (key) {
                case "VERSION":
                    request.setVersion(value);
                    break;
                case "LAYERS":
                    List<String> layers = Arrays.asList(value.split(","));
                    request.setLayers(layers);
                    break;
                case "STYLES":
                    List<String> styles = Arrays.asList(value.split(","));
                    request.setStyles(styles);
                    break;
                case "CRS":
                    request.setCrs(value);
                    break;
                case "BBOX":
                    String[] bbox = value.split(",");
                    request.setBbox(Double.parseDouble(bbox[0]), Double.parseDouble(bbox[1]),
                            Double.parseDouble(bbox[2]), Double.parseDouble(bbox[3]));
                    break;
                case "WIDTH":
                    request.setWidth(Integer.parseInt(value));
                    break;
                case "HEIGHT":
                    request.setHeight(Integer.parseInt(value));
                    break;
                case "FORMAT":
                    request.setFormat(value);
                    break;
                case "TRANSPARENT":
                    request.setTransparent(Boolean.parseBoolean(value));
                    break;
            }
        }
        return request;
    }
}
