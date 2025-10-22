package ir.msob.manak.dms.documentspecification;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.msob.jima.core.commons.client.BaseAsyncClient;
import ir.msob.jima.core.commons.operation.ConditionalOnOperation;
import ir.msob.jima.core.commons.resource.Resource;
import ir.msob.jima.core.commons.shared.ResourceType;
import ir.msob.jima.crud.api.kafka.client.ChannelUtil;
import ir.msob.manak.core.service.jima.crud.kafka.domain.service.DomainCrudKafkaListener;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecification;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationCriteria;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationDto;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationTypeReference;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import static ir.msob.jima.core.commons.operation.Operations.*;

@Component
@ConditionalOnOperation(operations = {SAVE, UPDATE_BY_ID, DELETE_BY_ID})
@Resource(value = DocumentSpecification.DOMAIN_NAME_WITH_HYPHEN, type = ResourceType.KAFKA)
public class DocumentSpecificationKafkaListener
        extends DomainCrudKafkaListener<DocumentSpecification, DocumentSpecificationDto, DocumentSpecificationCriteria, DocumentSpecificationRepository, DocumentSpecificationService>
        implements DocumentSpecificationTypeReference {
    public static final String BASE_URI = ChannelUtil.getBaseChannel(DocumentDto.class);

    protected DocumentSpecificationKafkaListener(UserService userService, DocumentSpecificationService service, ObjectMapper objectMapper, ConsumerFactory<String, String> consumerFactory, BaseAsyncClient asyncClient) {
        super(userService, service, objectMapper, consumerFactory, asyncClient);
    }
}
