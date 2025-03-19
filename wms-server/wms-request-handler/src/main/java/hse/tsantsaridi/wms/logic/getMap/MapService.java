package hse.tsantsaridi.wms.logic.getMap;

import hse.tsantsaridi.models.BoundingBox;
import hse.tsantsaridi.wms.repository.TileRepository;

public class MapService {

    private final TileRepository tileRepository;

    public MapService() {
        this.tileRepository = new TileRepository();
    }

    /**
     * Пример метода, который ищет все тайлы для запрошенного BBOX,
     * а затем решает, на какие ноды отправить запрос.
     */
    public void processGetMapRequest(BoundingBox boundingBox) {
//        try {
//            // Получаем список тайлов, пересекающихся с указанным BBOX
//            //List<Tile> tiles = tileRepository.findTilesByBBox(boundingBox);
//
////            for (Tile tile : tiles) {
////                System.out.println(tile);
////            }
////            // Для каждого тайла определяем, на какой node_id его искать
//            // и формируем gRPC-запросы (или HTTP-запросы) к соответствующим серверам
////            for (Tile tile : tiles) {
////                int nodeId = tile.getNodeId();
////                String tileId = tile.getTileId();
////
////                // Допустим, у нас есть метод, который по nodeId даёт нам адрес сервера:
////                String nodeAddress = getNodeAddress(nodeId);
////
////                // Далее вы формируете и отправляете запрос на nodeAddress,
////                // запрашивая tileId или соответствующий кусок данных.
////                // ...
////            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Логика обработки ошибки
//        }
    }

    /**
     * Условный метод для получения адреса ноды.
     * В реальном проекте это может быть запрос к БД, к другой таблице,
     * либо к некому реестру нод, либо к настройкам.
     */
    private String getNodeAddress(int nodeId) {
        // Заглушка для примера
        return "node-" + nodeId + ".mydomain.internal";
    }
}
