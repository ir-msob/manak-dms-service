package ir.msob.manak.dms.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.msob.jima.core.commons.id.BaseIdService;
import ir.msob.jima.core.commons.operation.BaseBeforeAfterDomainOperation;
import ir.msob.jima.crud.service.domain.BeforeAfterComponent;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.crud.base.childdomain.ChildDomainCrudService;
import ir.msob.manak.core.service.jima.crud.base.domain.DomainCrudService;
import ir.msob.manak.core.service.jima.service.IdService;
import ir.msob.manak.domain.model.dms.document.Document;
import ir.msob.manak.domain.model.dms.document.DocumentCriteria;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Service
public class DocumentService extends DomainCrudService<Document, DocumentDto, DocumentCriteria, DocumentRepository>
        implements ChildDomainCrudService<DocumentDto> {

    private final ModelMapper modelMapper;
    private final IdService idService;

    protected DocumentService(BeforeAfterComponent beforeAfterComponent, ObjectMapper objectMapper, DocumentRepository repository, ModelMapper modelMapper, IdService idService) {
        super(beforeAfterComponent, objectMapper, repository);
        this.modelMapper = modelMapper;
        this.idService = idService;
    }

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
}
