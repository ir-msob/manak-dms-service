package ir.msob.manak.dms;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestFilePart implements FilePart {

    private final String name;
    private final File file;
    private final HttpHeaders headers;
    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    private final int chunkSize = 4096;

    public TestFilePart(String name, File file, MediaType contentType) {
        this.name = Objects.requireNonNull(name);
        this.file = Objects.requireNonNull(file);
        this.headers = new HttpHeaders();
        this.headers.setContentDispositionFormData(name, file.getName());
        if (contentType != null) {
            this.headers.setContentType(contentType);
        }
        this.headers.setContentLength(file.length());
    }

    @Override
    public String filename() {
        return file.getName();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public Flux<DataBuffer> content() {
        Path p = Paths.get(file.toURI());
        // DataBufferUtils.read will stream file content as DataBuffer
        return DataBufferUtils.read(p, bufferFactory, chunkSize);
    }

    @Override
    public Mono<Void> transferTo(Path dest) {
        // simple blocking copy wrapped in Mono to satisfy contract
        return Mono.fromRunnable(() -> {
            try {
                java.nio.file.Files.copy(file.toPath(), dest);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
