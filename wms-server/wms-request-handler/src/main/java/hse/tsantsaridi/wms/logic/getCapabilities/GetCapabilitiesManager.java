package hse.tsantsaridi.wms.logic.getCapabilities;

import com.thoughtworks.xstream.XStream;
import hse.tsantsaridi.wms.models.capabilities.*;
import hse.tsantsaridi.wms.repository.MetadataRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;


public class GetCapabilitiesManager {
    private final String XML_FILE_PATH = "src/main/resources/wms_capabilities.xml";
    private final MetadataRepository metadataRepo = new MetadataRepository();

    public byte[] getMetadata() throws IOException, SQLException {
        File xmlFile = new File(XML_FILE_PATH);
        if (xmlFile.exists()) {
            return Files.readAllBytes(Paths.get(XML_FILE_PATH));
        } else {
            String xmlContent = fetchDataFromDatabaseAndGenerateXML();
            saveXMLToFile(xmlContent);

            return xmlContent.getBytes();
        }
    }

    private String fetchDataFromDatabaseAndGenerateXML() throws SQLException {
        List<ServiceMetadata> servicesMetadata = metadataRepo.getServiceMetadataAsXml();

        // Сериализация в XML
        XStream xStream = new XStream();
        xStream.alias("serviceMetadata", ServiceMetadata.class);
        xStream.alias("capability", Capability.class);
        xStream.alias("layer", Layer.class);
        xStream.alias("style", Style.class);
        xStream.alias("boundingBox", BoundingBox.class);
        return xStream.toXML(servicesMetadata);
    }

    // Сохранение сгенерированного XML в файл
    private void saveXMLToFile(String xmlContent) throws IOException {
        try (FileWriter writer = new FileWriter(XML_FILE_PATH)) {
            writer.write(xmlContent);
        }
    }
}
