package ir.msob.manak.dms.documentspecification;

import ir.msob.jima.core.commons.resource.BaseResource;
import ir.msob.jima.core.test.CoreTestData;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.test.jima.crud.restful.childdomain.BaseCharacteristicCrudRestResourceTest;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecification;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationCriteria;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationDto;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationTypeReference;
import ir.msob.manak.dms.Application;
import ir.msob.manak.dms.ContainerConfiguration;
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
public class DocumentSpecificationCharacteristicRestResourceIT
        extends BaseCharacteristicCrudRestResourceTest<DocumentSpecification, DocumentSpecificationDto, DocumentSpecificationCriteria, DocumentSpecificationRepository, DocumentSpecificationService, DocumentSpecificationDataProvider, DocumentSpecificationService, DocumentSpecificationCharacteristicCrudDataProvider>
        implements DocumentSpecificationTypeReference {

    @SneakyThrows
    @BeforeAll
    public static void beforeAll() {
        CoreTestData.init(new ObjectId(), new ObjectId());
    }

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        getDataProvider().cleanups();
        DocumentSpecificationDataProvider.createMandatoryNewDto();
        DocumentSpecificationDataProvider.createNewDto();
        DocumentSpecificationCharacteristicCrudDataProvider.createNewChild();
    }


    @Override
    public String getBaseUri() {
        return DocumentSpecificationRestResource.BASE_URI;
    }

    @Override
    public Class<? extends BaseResource<String, User>> getResourceClass() {
        return DocumentSpecificationCharacteristicRestResource.class;
    }

}
