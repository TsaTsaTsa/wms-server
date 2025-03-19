package hse.tsantsaridi.wms;

import java.util.logging.Level;
import java.util.logging.Logger;

import hse.tsantsaridi.wms.controller.http.HttpServer;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        int port = args.length > 0
                ? Integer.parseInt(args[0]) : 8080;
        try {
            new HttpServer(port).run();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Server interrupted while running on port " + port, e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred", e);
            System.exit(1);
        }
//        MapService mapService = new MapService();
//        List<BoundingBox> bbList = new ArrayList();
//        bbList.add(new BoundingBox(12, 22, 18, 28));
//        bbList.add(new BoundingBox(11, 21, 18, 24));
//        bbList.add(new BoundingBox(10, 20, 15, 20));
//        bbList.add(new BoundingBox(16, 20, 19, 28));
//
//        for (BoundingBox bb : bbList) {
//            System.out.println("Get " + bb);
//            mapService.processGetMapRequest(bb);
//        }
//        TileClient tc = new TileClient();
//        List<String> tiles = Arrays.asList("apple", "banana", "cherry");
//
//        BoundingBox bbox = new BoundingBox(10.0, 20.0, 30.0, 40.0);
//
//        // Список слоев и стилей
//        List<String> layers = Arrays.asList("roads", "buildings");
//        List<String> styles = Arrays.asList("default", "night");
//
//        // Создаем объект GetMapRequest с необходимыми параметрами
//        GetMapRequest request = new GetMapRequest(
//                "1.1.0",            // версия
//                layers,             // слои
//                styles,             // стили
//                "EPSG:4326",        // система координат
//                bbox,               // BBOX
//                800,                // ширина изображения
//                600,                // высота изображения
//                "image/png",        // формат
//                true                // прозрачность
//        );
//
//        System.out.println(tc.getTiles(new TileGroup("localhost", 50051, tiles), request));
    }
}