package ir.msob.manak.dms.documentspecification;

import ir.msob.manak.core.test.jima.crud.base.childdomain.characteristic.BaseCharacteristicCrudDataProvider;
import ir.msob.manak.domain.model.dms.documentspecification.DocumentSpecificationDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentSpecificationCharacteristicCrudDataProvider extends BaseCharacteristicCrudDataProvider<DocumentSpecificationDto, DocumentSpecificationService> {
    public DocumentSpecificationCharacteristicCrudDataProvider(DocumentSpecificationService childService) {
        super(childService);
    }
}
