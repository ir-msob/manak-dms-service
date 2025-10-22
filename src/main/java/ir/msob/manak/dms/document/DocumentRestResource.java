package ir.msob.manak.dms.document;


import ir.msob.jima.core.commons.operation.ConditionalOnOperation;
import ir.msob.jima.core.commons.resource.Resource;
import ir.msob.jima.core.commons.shared.ResourceType;
import ir.msob.manak.core.service.jima.crud.restful.domain.service.DomainCrudRestResource;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.dms.document.Document;
import ir.msob.manak.domain.model.dms.document.DocumentCriteria;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ir.msob.jima.core.commons.operation.Operations.*;

@RestController
@RequestMapping(DocumentRestResource.BASE_URI)
@ConditionalOnOperation(operations = {SAVE, UPDATE_BY_ID, DELETE_BY_ID, EDIT_BY_ID, GET_BY_ID, GET_PAGE})
@Resource(value = Document.DOMAIN_NAME_WITH_HYPHEN, type = ResourceType.RESTFUL)
public class DocumentRestResource extends DomainCrudRestResource<Document, DocumentDto, DocumentCriteria, DocumentRepository, DocumentService> {
    public static final String BASE_URI = "/api/v1/" + Document.DOMAIN_NAME_WITH_HYPHEN;

    protected DocumentRestResource(UserService userService, DocumentService service) {
        super(userService, service);
    }
}
