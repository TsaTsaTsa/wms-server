package hse.tsantsaridi.logic.geotiff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.gdal.WarpOptions;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogr;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wms.TileServiceOuterClass;

public class GetTilesManager {
    private static final Logger logger = LoggerFactory.getLogger(GetTilesManager.class);
    private static final String TILES_DIR = System.getenv().getOrDefault("TILES_DIR", "/app/data/tiles");
    private static final String STYLE_DIR = System.getenv().getOrDefault("STYLE_DIR", "/app/data/styles");
    private static final String IM_CMD = System.getenv().getOrDefault("IMAGE_MAGICK", "magick");

    public static void initializeGDAL() {
        logger.info("Registering GDAL and OGR drivers");
        gdal.AllRegister();
        ogr.RegisterAll();
        logger.debug("GDAL and OGR initialization complete");
    }

    private static Path applyStyle(Path src, String styleName) throws IOException, InterruptedException {
        logger.info("Applying style '{}' to image {}", styleName, src);
        Path lut = Path.of(STYLE_DIR + "/" + styleName + "_style.png");
        if (!Files.exists(lut)) {
            logger.error("Style file not found: {}", lut);
            throw new IOException("Unknown style: " + styleName);
        }
        Path dst = Files.createTempFile("styled_", ".png");
        logger.debug("Created temporary style output file {}", dst);
        ProcessBuilder pb = new ProcessBuilder(
                IM_CMD,
                src.toAbsolutePath().toString(),
                lut.toAbsolutePath().toString(),
                "-clut",
                dst.toAbsolutePath().toString()
        );
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process p = pb.start();
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            logger.error("ImageMagick process exited with code {}", exitCode);
            throw new IOException("ImageMagick error, exit code " + exitCode);
        }
        logger.debug("Style applied successfully, output at {}", dst);
        return dst;
    }

    public static Geometry parseBoundingBox(String wkt) throws IOException {
        logger.info("Parsing bounding box WKT: {}", wkt);
        Geometry geom = Geometry.CreateFromWkt(wkt);
        if (geom == null) {
            logger.error("Failed to parse WKT: {}", wkt);
            throw new IOException("Invalid bounding box format: " + wkt);
        }
        logger.debug("Bounding box geometry created");
        return geom;
    }

    public static Dataset openTile(String tileId) {
        String filePath = TILES_DIR + tileId + ".tif";
        logger.info("Opening tile file {}", filePath);
        Dataset ds = gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
        if (ds == null) {
            logger.warn("GDAL could not open tile dataset for {}", tileId);
        } else {
            logger.debug("Tile dataset opened successfully for {}", tileId);
        }
        return ds;
    }

    public static Geometry getTileGeometry(Dataset tileDs) {
        logger.debug("Computing tile geometry from Dataset");
        double[] geoTransform = tileDs.GetGeoTransform();
        int rasterXSize = tileDs.getRasterXSize();
        int rasterYSize = tileDs.getRasterYSize();
        double tileXMin = geoTransform[0];
        double tileYMax = geoTransform[3];
        double tileXMax = tileXMin + geoTransform[1] * rasterXSize;
        double tileYMin = tileYMax + geoTransform[5] * rasterYSize;
        String tilePolygonWKT = String.format(
                "POLYGON ((%f %f, %f %f, %f %f, %f %f, %f %f))",
                tileXMin, tileYMin,
                tileXMin, tileYMax,
                tileXMax, tileYMax,
                tileXMax, tileYMin,
                tileXMin, tileYMin
        );
        logger.debug("Tile geometry WKT: {}", tilePolygonWKT);
        Geometry geom = Geometry.CreateFromWkt(tilePolygonWKT);
        logger.debug("Tile geometry created");
        return geom;
    }

    public static int[] computeCropParameters(Dataset tileDs, Geometry intersection) {
        logger.debug("Calculating crop parameters for intersection");
        double[] geoTransform = tileDs.GetGeoTransform();
        int rasterXSize = tileDs.getRasterXSize();
        int rasterYSize = tileDs.getRasterYSize();
        double tileXMin = geoTransform[0];
        double tileYMax = geoTransform[3];
        double pixelWidth = geoTransform[1];
        double pixelHeight = geoTransform[5];
        double[] envelope = new double[4];
        intersection.GetEnvelope(envelope);
        int xOff = (int) Math.floor((envelope[0] - tileXMin) / pixelWidth);
        int yOff = (int) Math.floor((tileYMax - envelope[3]) / Math.abs(pixelHeight));
        int xSize = (int) Math.ceil((envelope[1] - envelope[0]) / pixelWidth);
        int ySize = (int) Math.ceil((envelope[3] - envelope[2]) / Math.abs(pixelHeight));
        if (xOff < 0) xOff = 0;
        if (yOff < 0) yOff = 0;
        if (xOff + xSize > rasterXSize) xSize = rasterXSize - xOff;
        if (yOff + ySize > rasterYSize) ySize = rasterYSize - yOff;
        logger.debug("Crop window [xOff={}, yOff={}, xSize={}, ySize={}]", xOff, yOff, xSize, ySize);
        return new int[]{xOff, yOff, xSize, ySize};
    }

    public static Dataset cropTile(Dataset tileDs, String tileId, int xOff, int yOff, int xSize, int ySize) throws IOException {
        logger.info("Cropping tile {} with parameters ({}, {}, {}, {})", tileId, xOff, yOff, xSize, ySize);
        Vector<String> translateOptionsVector = new Vector<>();
        translateOptionsVector.add("-srcwin");
        translateOptionsVector.add(String.valueOf(xOff));
        translateOptionsVector.add(String.valueOf(yOff));
        translateOptionsVector.add(String.valueOf(xSize));
        translateOptionsVector.add(String.valueOf(ySize));
        Path tempCroppedFile = Files.createTempFile("temp_", ".tif");
        Dataset ds = gdal.Translate(tempCroppedFile.toString(), tileDs, new TranslateOptions(translateOptionsVector));
        logger.debug("Temporary cropped tile written to {}", tempCroppedFile);
        return ds;
    }


    public static Dataset mosaicTiles(List<Dataset> croppedDatasets, Geometry overallGeom, String outputFile) {
        logger.info("Mosaicking {} tiles into {}", croppedDatasets.size(), outputFile);
        double[] overallEnvelope = new double[4];
        overallGeom.GetEnvelope(overallEnvelope);
        Vector<String> warpOptionsVector = new Vector<>();
        warpOptionsVector.add("-of");
        warpOptionsVector.add("PNG");
        warpOptionsVector.add("-te");
        warpOptionsVector.add(String.valueOf(overallEnvelope[0]));
        warpOptionsVector.add(String.valueOf(overallEnvelope[2]));
        warpOptionsVector.add(String.valueOf(overallEnvelope[1]));
        warpOptionsVector.add(String.valueOf(overallEnvelope[3]));
        logger.debug("Warp options: {}", warpOptionsVector);
        WarpOptions warpOptions = new WarpOptions(warpOptionsVector);
        Dataset[] srcArray = croppedDatasets.toArray(new Dataset[0]);
        Dataset mosaic = gdal.Warp(outputFile, srcArray, warpOptions);
        logger.debug("Mosaic TIFF created at {}", outputFile);
        return mosaic;
    }

    public static void cleanupTemporaryFiles() {
        logger.info("Cleaning up temporary tile files");

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(
                Paths.get(System.getProperty("java.io.tmpdir")), "temp_*.tif")) {
            for (Path p : ds) {
                Files.deleteIfExists(p);
                logger.debug("Deleted temporary file {}", p);
            }
        }catch (IOException e) {
            logger.warn("Failed to delete temporary file: {}", e.getMessage());
        }
    }

    public static ByteString getTileData(TileServiceOuterClass.GetTilesRequest request) throws IOException {
        logger.info("getTileData started for {} tiles", request.getTileIdsCount());
        initializeGDAL();

        Geometry bbox = parseBoundingBox(request.getBoundingBoxWKT());
        List<Dataset> croppedDatasets = new ArrayList<>();
        //List<String> tempTileFiles = new ArrayList<>();

        for (String tileId : request.getTileIdsList()) {
            logger.debug("Processing tile {}", tileId);
            Dataset tileDs = openTile(tileId);
            if (tileDs == null) {
                logger.warn("Skipping missing tile {}", tileId);
                continue;
            }
            Geometry tileGeom = getTileGeometry(tileDs);
            Geometry intersection = tileGeom.Intersection(bbox);
            if (intersection == null || intersection.IsEmpty()) {
                logger.debug("No intersection for tile {}", tileId);
                tileDs.delete();
                continue;
            }
            int[] cropParams = computeCropParameters(tileDs, intersection);
            Dataset croppedDs = cropTile(tileDs, tileId, cropParams[0], cropParams[1], cropParams[2], cropParams[3]);
            //tempTileFiles.add(tileId);
            if (croppedDs != null) {
                croppedDatasets.add(croppedDs);
            }
            tileDs.delete();
        }

        if (croppedDatasets.isEmpty()) {
            logger.error("No tiles intersect with the requested bounding box");
            throw new IOException("No tiles to process");
        }

        Path mosaicTif = Files.createTempFile("mosaic_", ".tif");
        Dataset mosaicDs = mosaicTiles(croppedDatasets, bbox, mosaicTif.toString());

        croppedDatasets.forEach(Dataset::delete);
        cleanupTemporaryFiles();

        Path png;
        if (!request.getStyles(0).isEmpty()) {
            logger.info("Applying style to mosaic TIFF {}", mosaicTif);
            mosaicDs.delete();
            try {
                png = applyStyle(mosaicTif, request.getStyles(0));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Style application interrupted", e);
                throw new IOException("ImageMagick interrupted", e);
            }
        } else {
            png = Files.createTempFile("mosaic_", ".png");
            Vector<String> opts = new Vector<>(List.of("-of", "PNG"));
            logger.info("Converting mosaic TIFF {} to PNG {}", mosaicTif, png);
            gdal.Translate(
                    png.toString(),
                    gdal.Open(mosaicTif.toString()),
                    new TranslateOptions(opts)
            );
            mosaicDs.delete();
        }

        byte[] bytes = Files.readAllBytes(png);
        Files.deleteIfExists(Paths.get(mosaicTif.toString()));
        Files.deleteIfExists(Paths.get(png.toString()));
        logger.info("getTileData completed, output size {} bytes", bytes.length);

        return ByteString.copyFrom(bytes);
    }
}