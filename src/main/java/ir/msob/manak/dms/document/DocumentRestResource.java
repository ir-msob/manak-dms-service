package ir.msob.manak.dms.document;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import ir.msob.jima.core.commons.exception.badrequest.BadRequestResponse;
import ir.msob.jima.core.commons.methodstats.MethodStats;
import ir.msob.jima.core.commons.operation.ConditionalOnOperation;
import ir.msob.jima.core.commons.operation.Operations;
import ir.msob.jima.core.commons.resource.Resource;
import ir.msob.jima.core.commons.scope.Scope;
import ir.msob.jima.core.commons.shared.ResourceType;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.crud.restful.domain.service.DomainCrudRestResource;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.dms.document.Document;
import ir.msob.manak.domain.model.dms.document.DocumentCriteria;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static ir.msob.jima.core.commons.operation.Operations.*;

@RestController
@RequestMapping(DocumentRestResource.BASE_URI)
@ConditionalOnOperation(operations = {SAVE, UPDATE_BY_ID, DELETE_BY_ID, EDIT_BY_ID, GET_BY_ID, GET_PAGE})
@Resource(value = Document.DOMAIN_NAME_WITH_HYPHEN, type = ResourceType.RESTFUL)
public class DocumentRestResource extends DomainCrudRestResource<Document, DocumentDto, DocumentCriteria, DocumentRepository, DocumentService> {
    public static final String BASE_URI = "/api/v1/" + Document.DOMAIN_NAME_WITH_HYPHEN;
    Logger log = LoggerFactory.getLogger(DocumentRestResource.class);

    protected DocumentRestResource(UserService userService, DocumentService service) {
        super(userService, service);
    }


    @PostMapping("{id}/upload-attachment")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Return a domain or null"),
            @ApiResponse(code = 400, message = "If the validation operation is incorrect throws BadRequestException otherwise nothing", response = BadRequestResponse.class)})
    @Scope(operation = Operations.SAVE)
    @MethodStats
    public Mono<ResponseEntity<DocumentDto>> uploadAttachment(@RequestPart(value = "file") FilePart file,
                                                              @PathVariable("id") String id,
                                                              Principal principal) {
        log.debug("REST request to upload attachment, id {}", id);
        User user = getUser(principal);
        return this.getService().uploadAttachment(id, file, user).map(ResponseEntity::ok);
    }
}
