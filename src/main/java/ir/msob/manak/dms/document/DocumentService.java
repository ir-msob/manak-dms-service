package ir.msob.manak.dms.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.msob.jima.core.commons.exception.datanotfound.DataNotFoundException;
import ir.msob.jima.core.commons.id.BaseIdService;
import ir.msob.jima.core.commons.operation.BaseBeforeAfterDomainOperation;
import ir.msob.jima.crud.service.domain.BeforeAfterComponent;
import ir.msob.jima.storage.commons.StoredFileInfo;
import ir.msob.manak.core.model.jima.childdomain.relatedobject.relateddomain.RelatedDomain;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.crud.base.childdomain.ChildDomainCrudService;
import ir.msob.manak.core.service.jima.crud.base.domain.DomainCrudService;
import ir.msob.manak.core.service.jima.service.IdService;
import ir.msob.manak.dms.file.FileService;
import ir.msob.manak.domain.model.dms.document.Document;
import ir.msob.manak.domain.model.dms.document.DocumentCriteria;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import ir.msob.manak.domain.model.dms.document.attachment.Attachment;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentService extends DomainCrudService<Document, DocumentDto, DocumentCriteria, DocumentRepository>
        implements ChildDomainCrudService<DocumentDto> {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final ModelMapper modelMapper;
    private final IdService idService;
    private final FileService fileService;

    protected DocumentService(BeforeAfterComponent beforeAfterComponent,
                              ObjectMapper objectMapper,
                              DocumentRepository repository,
                              ModelMapper modelMapper,
                              IdService idService,
                              FileService fileService) {
        super(beforeAfterComponent, objectMapper, repository);
        this.modelMapper = modelMapper;
        this.idService = idService;
        this.fileService = fileService;
    }

    // ---------------------------------------------------------------------------------------------
    // 🔹 Mapping Methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public DocumentDto toDto(Document domain, User user) {
        return modelMapper.map(domain, DocumentDto.class);
    }

    @Override
    public Document toDomain(DocumentDto dto, User user) {
        return dto;
    }

    @Override
    public Collection<BaseBeforeAfterDomainOperation<String, User, DocumentDto, DocumentCriteria>> getBeforeAfterDomainOperations() {
        return Collections.emptyList();
    }

    // ---------------------------------------------------------------------------------------------
    // 🔹 CRUD Overrides
    // ---------------------------------------------------------------------------------------------
    @Transactional
    @Override
    public Mono<DocumentDto> getDto(String id, User user) {
        return getOne(id, user);
    }

    @Transactional
    @Override
    public Mono<DocumentDto> updateDto(String id, @Valid DocumentDto dto, User user) {
        return update(id, dto, user);
    }

    @Override
    public BaseIdService getIdService() {
        return idService;
    }

    // ---------------------------------------------------------------------------------------------
    // 🔹 Upload Attachment
    // ---------------------------------------------------------------------------------------------
    @Transactional
    public Mono<DocumentDto> uploadAttachment(String id, FilePart file, User user) {
        log.info("Starting attachment upload for documentId={}, filename={}", id, file.filename());

        return getOne(id, user)
                .flatMap(documentDto -> {
                    RelatedDomain primaryRelatedDomain = findPrimaryRelatedDomain(documentDto, id);

                    String filePath = prepareFilePath(primaryRelatedDomain, file);
                    log.debug("Prepared file path: {}", filePath);

                    return fileService.store(primaryRelatedDomain.getName(), filePath, file, user)
                            .flatMap(storedFileInfo -> {
                                Attachment newAttachment = createNewAttachment(documentDto, storedFileInfo);
                                documentDto.getAttachments().add(newAttachment);

                                log.info("Attachment [{}] (v{}) added to document [{}]",
                                        newAttachment.getFileName(), newAttachment.getVersion(), id);

                                return save(documentDto, user);
                            });
                })
                .doOnSuccess(dto -> log.info("✅ Attachment uploaded successfully for document [{}]", id))
                .doOnError(e -> log.error("❌ Failed to upload attachment for document [{}]: {}", id, e.getMessage(), e));
    }

    // ---------------------------------------------------------------------------------------------
    // 🔹 Helper Methods
    // ---------------------------------------------------------------------------------------------
    private RelatedDomain findPrimaryRelatedDomain(DocumentDto documentDto, String documentId) {
        return documentDto.getRelatedDomains().stream()
                .filter(rd -> Document.RelatedDomainRole.PRIMARY.name().equalsIgnoreCase(rd.getRole()))
                .findFirst()
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Primary related domain not found for document with ID: " + documentId,
                                DocumentDto.class.getSimpleName(),
                                documentId
                        ));
    }

    private static @NotNull AtomicInteger prepareVersion(DocumentDto documentDto) {
        AtomicInteger version = new AtomicInteger(0);
        documentDto.getLatestAttachment()
                .ifPresent(a -> version.set(a.getVersion() + 1));
        return version;
    }

    private Attachment createNewAttachment(DocumentDto documentDto, StoredFileInfo storedFileInfo) {
        int version = prepareVersion(documentDto).get();

        return Attachment.builder()
                .status(Attachment.Status.CREATED)
                .filePath(storedFileInfo.getFilePath())
                .mimeType(storedFileInfo.getMimeType())
                .fileSize(storedFileInfo.getFileSize())
                .fileName(storedFileInfo.getFileName())
                .version(version)
                .build();
    }

    private String prepareFilePath(RelatedDomain relatedDomain, FilePart file) {
        List<String> segments = Stream.of(
                        relatedDomain.getReferringType(),
                        relatedDomain.getRelatedId(),
                        relatedDomain.getName())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());

        // Join non-empty segments with "/"
        String basePath = String.join("/", segments);
        String fileName = file.filename();

        String fullPath = (basePath.isEmpty() ? fileName : basePath + "/" + fileName);
        log.debug("Generated file path: {}", fullPath);
        return fullPath;
    }
}
