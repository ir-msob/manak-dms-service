package ir.msob.manak.dms.documentspecification;


import ir.msob.jima.core.commons.operation.ConditionalOnOperation;
import ir.msob.jima.core.commons.resource.Resource;
import ir.msob.jima.core.commons.shared.ResourceType;
import ir.msob.manak.core.service.jima.crud.restful.domain.service.DomainCrudRestResource;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecification;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationCriteria;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ir.msob.jima.core.commons.operation.Operations.*;

@RestController
@RequestMapping(DocumentSpecificationRestResource.BASE_URI)
@ConditionalOnOperation(operations = {SAVE, UPDATE_BY_ID, DELETE_BY_ID, EDIT_BY_ID, GET_BY_ID, GET_PAGE})
@Resource(value = DocumentSpecification.DOMAIN_NAME_WITH_HYPHEN, type = ResourceType.RESTFUL)
public class DocumentSpecificationRestResource extends DomainCrudRestResource<DocumentSpecification, DocumentSpecificationDto, DocumentSpecificationCriteria, DocumentSpecificationRepository, DocumentSpecificationService> {
    public static final String BASE_URI = "/api/v1/" + DocumentSpecification.DOMAIN_NAME_WITH_HYPHEN;

    protected DocumentSpecificationRestResource(UserService userService, DocumentSpecificationService service) {
        super(userService, service);
    }
}
