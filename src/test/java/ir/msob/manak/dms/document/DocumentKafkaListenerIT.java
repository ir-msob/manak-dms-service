package ir.msob.manak.dms.document;

import ir.msob.jima.core.commons.resource.BaseResource;
import ir.msob.jima.core.test.CoreTestData;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.test.jima.crud.kafka.domain.DomainCrudKafkaListenerTest;
import ir.msob.manak.dms.Application;
import ir.msob.manak.dms.ContainerConfiguration;
import ir.msob.manak.domain.model.dms.document.Document;
import ir.msob.manak.domain.model.dms.document.DocumentCriteria;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import ir.msob.manak.domain.model.dms.document.DocumentTypeReference;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {Application.class, ContainerConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CommonsLog
public class DocumentKafkaListenerIT
        extends DomainCrudKafkaListenerTest<Document, DocumentDto, DocumentCriteria, DocumentRepository, DocumentService, DocumentDataProvider>
        implements DocumentTypeReference {

    @SneakyThrows
    @BeforeAll
    public static void beforeAll() {
        CoreTestData.init(new ObjectId(), new ObjectId());
    }

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        getDataProvider().cleanups();
        DocumentDataProvider.createMandatoryNewDto();
        DocumentDataProvider.createNewDto();
    }

    @Override
    public Class<? extends BaseResource<String, User>> getResourceClass() {
        return DocumentKafkaListener.class;
    }

    @Override
    public String getBaseUri() {
        return DocumentKafkaListener.BASE_URI;
    }
}
