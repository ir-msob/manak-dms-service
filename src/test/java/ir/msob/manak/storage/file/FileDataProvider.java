package ir.msob.manak.storage.file;


import ir.msob.jima.core.commons.util.ImageFile;
import ir.msob.jima.core.commons.util.TempFile;
import ir.msob.jima.core.commons.util.TextFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileDataProvider {

    public static final String REQUEST_PART_KEY = "file";
    public static final String DEFAULT_RELATED_DOMAIN_TYPE = "document";
    public static final String TEXT_FILE_NAME = "file.txt";
    public static final String IMAGE_FILE_NAME = "file.jpg";
    public static final String XML_FILE_NAME = "file.xml";
    public static final TempFile TEMP_FILE;
    public static File TEXT_FILE;
    public static File IMAGE_FILE;

    static {
        try {
            TEMP_FILE = new TempFile("TEST");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeUploadFiles() throws IOException {
        TEMP_FILE.close();
    }

    public static void createUploadFiles() throws IOException {
        TEXT_FILE = TEMP_FILE.createFileObject(TEXT_FILE_NAME);
        TextFile.write(TEXT_FILE, 100);
        IMAGE_FILE = TEMP_FILE.createFileObject(IMAGE_FILE_NAME);
        ImageFile.create(IMAGE_FILE);
    }

    public BodyInserters.MultipartInserter prepareUploadRequest(File file) {
        FileSystemResource resource = new FileSystemResource(file);
        MultiValueMap<String, Resource> request = new LinkedMultiValueMap<>();
        request.set(REQUEST_PART_KEY, resource);
        return BodyInserters.fromMultipartData(request);
    }

}
