package ir.msob.manak.dms;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import ir.msob.jima.core.beans.properties.JimaProperties;
import ir.msob.jima.core.ral.kafka.test.KafkaContainerConfiguration;
import ir.msob.jima.core.ral.minio.test.MinIOContainerConfiguration;
import ir.msob.jima.core.ral.mongo.test.configuration.MongoContainerConfiguration;
import ir.msob.jima.security.ral.keycloak.test.KeycloakContainerConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration
public class ContainerConfiguration {
    @Bean
    public DynamicPropertyRegistrar dynamicPropertyRegistrar(JimaProperties jimaProperties
            , KeycloakContainer keycloakContainer
            , MinIOContainer minIOContainer
            , KafkaContainer kafkaContainer
            , MongoDBContainer mongoDBContainer
    ) {
        return registry -> {
            KeycloakContainerConfiguration.registry(registry, keycloakContainer, jimaProperties);
            KafkaContainerConfiguration.registry(registry, kafkaContainer);
            MinIOContainerConfiguration.registry(registry, minIOContainer);
            MongoContainerConfiguration.registry(registry, mongoDBContainer);
        };
    }
}
