//package hse.tsantsaridi.logic.geotiff;
//
//import org.gdal.ogr.Geometry;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GetTilesManagerTest {
//    GetTilesManager gtm = new GetTilesManager();
//
//    @ParameterizedTest
//    @CsvSource({
//            "'POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))', true",
//            "'POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))', true",
//            "'POLYGON ((-10 -10, -10 10, 10 10, 10 -10, -10 -10))', true",
//            "'POLYGON ((0 0, 0 1, 1 1, 1))', false",
//            "'POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0), (0.5 0.5, 0.5 0.8, 0.8 0.8, 0.8 0.5, 0.5 0.5))', true"
//    })
//    void testParseBoundingBox(String wkt, boolean isValid) {
//        try {
//            Geometry geom = gtm.parseBoundingBox(wkt);
//            if (!isValid) {
//                fail("Ожидалась ошибка, но геометрия была успешно разобрана: " + geom);
//            }
//        } catch (IOException e) {
//            if (isValid) {
//                fail("Ожидалась успешная обработка, но произошла ошибка: " + e.getMessage());
//            }
//        }
//    }
//
//    @Test
//    void openTile() {
//    }
//}