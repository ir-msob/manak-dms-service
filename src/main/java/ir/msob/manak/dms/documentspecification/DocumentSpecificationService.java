package ir.msob.manak.dms.documentspecification;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.msob.jima.core.commons.id.BaseIdService;
import ir.msob.jima.core.commons.operation.BaseBeforeAfterDomainOperation;
import ir.msob.jima.crud.service.domain.BeforeAfterComponent;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.crud.base.childdomain.ChildDomainCrudService;
import ir.msob.manak.core.service.jima.crud.base.domain.DomainCrudService;
import ir.msob.manak.core.service.jima.service.IdService;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecification;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationCriteria;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Service
public class DocumentSpecificationService extends DomainCrudService<DocumentSpecification, DocumentSpecificationDto, DocumentSpecificationCriteria, DocumentSpecificationRepository>
        implements ChildDomainCrudService<DocumentSpecificationDto> {

    private final ModelMapper modelMapper;
    private final IdService idService;

    protected DocumentSpecificationService(BeforeAfterComponent beforeAfterComponent, ObjectMapper objectMapper, DocumentSpecificationRepository repository, ModelMapper modelMapper, IdService idService) {
        super(beforeAfterComponent, objectMapper, repository);
        this.modelMapper = modelMapper;
        this.idService = idService;
    }

    @Override
    public DocumentSpecificationDto toDto(DocumentSpecification domain, User user) {
        return modelMapper.map(domain, DocumentSpecificationDto.class);
    }

    @Override
    public DocumentSpecification toDomain(DocumentSpecificationDto dto, User user) {
        return dto;
    }

    @Override
    public Collection<BaseBeforeAfterDomainOperation<String, User, DocumentSpecificationDto, DocumentSpecificationCriteria>> getBeforeAfterDomainOperations() {
        return Collections.emptyList();
    }


    @Transactional
    @Override
    public Mono<DocumentSpecificationDto> getDto(String id, User user) {
        return getOne(id, user);
    }

    @Transactional
    @Override
    public Mono<DocumentSpecificationDto> updateDto(String id, @Valid DocumentSpecificationDto dto, User user) {
        return update(id, dto, user);
    }

    @Override
    public BaseIdService getIdService() {
        return idService;
    }
}
