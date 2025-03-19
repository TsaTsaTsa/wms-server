package hse.tsantsaridi.logic.geotiff;

import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.IOException;
import java.util.Vector;

import com.google.protobuf.ByteString;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdal.WarpOptions;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogr;
import wms.TileServiceOuterClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GetTilesManager {
    private static final String TILES_DIR = System.getenv("TILES_DIR");

    public static void initializeGDAL() {
        gdal.AllRegister();
        ogr.RegisterAll();
    }

    /**
     * Разбирает bounding box из WKT-строки и возвращает соответствующую геометрию.
     */
    public static Geometry parseBoundingBox(String wkt) throws IOException {
        Geometry geom = Geometry.CreateFromWkt(wkt);
        if (geom == null) {
            throw new IOException("Некорректный формат bounding box: " + wkt);
        }
        return geom;
    }

    /**
     * Открывает файл-тайл по его идентификатору.
     * Предполагается, что имя файла формируется как TILES_DIR + tileId + ".tif".
     */
    public static Dataset openTile(String tileId) {
        String filePath = TILES_DIR + tileId + ".tif";
        return gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
    }

    /**
     * Создает геометрию (полигон) для тайла на основе его геотрансформации.
     */
    public static Geometry getTileGeometry(Dataset tileDs) {
        double[] geoTransform = tileDs.GetGeoTransform();
        int rasterXSize = tileDs.getRasterXSize();
        int rasterYSize = tileDs.getRasterYSize();
        double tileXMin = geoTransform[0];
        double tileYMax = geoTransform[3];
        double tileXMax = tileXMin + geoTransform[1] * rasterXSize;
        double tileYMin = tileYMax + geoTransform[5] * rasterYSize;

        String tilePolygonWKT = String.format("POLYGON ((%f %f, %f %f, %f %f, %f %f, %f %f))",
                tileXMin, tileYMin,
                tileXMin, tileYMax,
                tileXMax, tileYMax,
                tileXMax, tileYMin,
                tileXMin, tileYMin);
        return Geometry.CreateFromWkt(tilePolygonWKT);
    }

    /**
     * Рассчитывает параметры обрезки (xOff, yOff, xSize, ySize) для тайла,
     * исходя из пересечения его области с заданным интервалом.
     */
    public static int[] computeCropParameters(Dataset tileDs, Geometry intersection) {
        double[] geoTransform = tileDs.GetGeoTransform();
        int rasterXSize = tileDs.getRasterXSize();
        int rasterYSize = tileDs.getRasterYSize();
        double tileXMin = geoTransform[0];
        double tileYMax = geoTransform[3];
        double pixelWidth = geoTransform[1];
        double pixelHeight = geoTransform[5];

        // Получаем envelope пересечения: [minX, maxX, minY, maxY]
        double[] envelope = new double[4];
        intersection.GetEnvelope(envelope);
        double interMinX = envelope[0];
        double interMaxX = envelope[1];
        double interMinY = envelope[2];
        double interMaxY = envelope[3];

        int xOff = (int) Math.floor((interMinX - tileXMin) / pixelWidth);
        int yOff = (int) Math.floor((tileYMax - interMaxY) / Math.abs(pixelHeight));
        int xSize = (int) Math.ceil((interMaxX - interMinX) / pixelWidth);
        int ySize = (int) Math.ceil((interMaxY - interMinY) / Math.abs(pixelHeight));

        // Корректировка, чтобы окно не выходило за пределы изображения
        if (xOff < 0) xOff = 0;
        if (yOff < 0) yOff = 0;
        if (xOff + xSize > rasterXSize) xSize = rasterXSize - xOff;
        if (yOff + ySize > rasterYSize) ySize = rasterYSize - yOff;

        return new int[]{xOff, yOff, xSize, ySize};
    }

    /**
     * Обрезает (crop) тайл с помощью gdal.Translate и сохраняет результат во временный файл.
     */
    public static Dataset cropTile(Dataset tileDs, String tileId, int xOff, int yOff, int xSize, int ySize) {
        Vector<String> translateOptionsVector = new Vector<>();
        translateOptionsVector.add("-srcwin");
        translateOptionsVector.add(String.valueOf(xOff));
        translateOptionsVector.add(String.valueOf(yOff));
        translateOptionsVector.add(String.valueOf(xSize));
        translateOptionsVector.add(String.valueOf(ySize));
        String tempCroppedFile = "temp_" + tileId + ".tif";

        return gdal.Translate(tempCroppedFile, tileDs, new TranslateOptions(translateOptionsVector));
    }

    /**
     * Объединяет (склеивает) обрезанные тайлы в одно изображение с помощью gdal.Warp.
     * Выходной файл сохраняется в формате PNG.
     */
    public static Dataset mosaicTiles(List<Dataset> croppedDatasets, Geometry overallGeom, String outputFile) throws IOException {
        double[] overallEnvelope = new double[4];
        overallGeom.GetEnvelope(overallEnvelope); // [minX, maxX, minY, maxY]
        Vector<String> warpOptionsVector = new Vector<>();
        warpOptionsVector.add("-of");
        warpOptionsVector.add("PNG");
        warpOptionsVector.add("-te");
        warpOptionsVector.add(String.valueOf(overallEnvelope[0])); // minX
        warpOptionsVector.add(String.valueOf(overallEnvelope[2])); // minY
        warpOptionsVector.add(String.valueOf(overallEnvelope[1])); // maxX
        warpOptionsVector.add(String.valueOf(overallEnvelope[3])); // maxY

        WarpOptions warpOptions = new WarpOptions(warpOptionsVector);
        Dataset[] srcArray = croppedDatasets.toArray(new Dataset[0]);
        return gdal.Warp(outputFile, srcArray, warpOptions);
    }

    /**
     * Очищает временные файлы, созданные в процессе обработки.
     */
    public static void cleanupTemporaryFiles(List<String> tileIds, String mosaicFile) {
        try {
            Files.deleteIfExists(Paths.get(mosaicFile));
        } catch (IOException e) {
            System.err.println("Не удалось удалить файл: " + mosaicFile);
        }
        for (String tileId : tileIds) {
            Path tempPath = Paths.get("temp_" + tileId + ".tif");
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException e) {
                System.err.println("Не удалось удалить временный файл: " + tempPath);
            }
        }
    }

    /**
     * Основной метод обработки запроса, который:
     * 1. Парсит общий bounding box.
     * 2. Для каждого tile_id открывает тайл, вычисляет пересечение и выполняет обрезку.
     * 3. Объединяет обрезанные тайлы в итоговое изображение.
     * 4. Считывает изображение в виде массива байтов и возвращает его в виде ByteString.
     *
     * @param request gRPC-запрос с параметрами.
     * @return ByteString с итоговым PNG-изображением.
     * @throws IOException при ошибках ввода-вывода.
     */
    public static ByteString getTileData(TileServiceOuterClass.GetTilesRequest request) throws IOException {
        initializeGDAL();

        Geometry overallGeom = parseBoundingBox(request.getBoundingBoxWKT());
        List<Dataset> croppedDatasets = new ArrayList<>();
        List<String> processedTileIds = new ArrayList<>();

        // Обрабатываем каждый запрошенный тайл
        for (String tileId : request.getTileIdsList()) {
            Dataset tileDs = openTile(tileId);
            if (tileDs == null) {
                System.err.println("Не удалось открыть файл для тайла: " + tileId);
                continue;
            }
            Geometry tileGeom = getTileGeometry(tileDs);
            if (tileGeom == null) {
                System.err.println("Не удалось создать геометрию для тайла: " + tileId);
                tileDs.delete();
                continue;
            }
            Geometry intersection = tileGeom.Intersection(overallGeom);
            if (intersection == null || intersection.IsEmpty()) {
                tileDs.delete();
                continue;
            }
            int[] cropParams = computeCropParameters(tileDs, intersection);
            Dataset croppedDs = cropTile(tileDs, tileId, cropParams[0], cropParams[1], cropParams[2], cropParams[3]);
            if (croppedDs != null) {
                croppedDatasets.add(croppedDs);
                processedTileIds.add(tileId);
            }
            tileDs.delete();
        }

        if (croppedDatasets.isEmpty()) {
            throw new IOException("Нет тайлов, пересекающихся с запрошенной областью.");
        }

        String outputFile = "mosaic.png";
        Dataset mosaicDs = mosaicTiles(croppedDatasets, overallGeom, outputFile);

        // Освобождаем ресурсы обрезанных тайлов
        for (Dataset ds : croppedDatasets) {
            ds.delete();
        }

        byte[] imageBytes = Files.readAllBytes(Paths.get(outputFile));
        cleanupTemporaryFiles(processedTileIds, outputFile);
        return ByteString.copyFrom(imageBytes);
    }
}
