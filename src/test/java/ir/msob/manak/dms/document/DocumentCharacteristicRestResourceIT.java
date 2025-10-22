package ir.msob.manak.dms.document;

import ir.msob.jima.core.commons.resource.BaseResource;
import ir.msob.jima.core.test.CoreTestData;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.test.jima.crud.restful.childdomain.BaseCharacteristicCrudRestResourceTest;
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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureWebTestClient
@SpringBootTest(classes = {Application.class, ContainerConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CommonsLog
public class DocumentCharacteristicRestResourceIT
        extends BaseCharacteristicCrudRestResourceTest<Document, DocumentDto, DocumentCriteria, DocumentRepository, DocumentService, DocumentDataProvider, DocumentService, DocumentCharacteristicCrudDataProvider>
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
        DocumentCharacteristicCrudDataProvider.createNewChild();
    }

    @Override
    public String getBaseUri() {
        return DocumentRestResource.BASE_URI;
    }

    @Override
    public Class<? extends BaseResource<String, User>> getResourceClass() {
        return DocumentCharacteristicRestResource.class;
    }
}
