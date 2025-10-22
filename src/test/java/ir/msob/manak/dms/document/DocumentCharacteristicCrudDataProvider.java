package ir.msob.manak.dms.document;

import ir.msob.manak.core.test.jima.crud.base.childdomain.characteristic.BaseCharacteristicCrudDataProvider;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentCharacteristicCrudDataProvider extends BaseCharacteristicCrudDataProvider<DocumentDto, DocumentService> {
    public DocumentCharacteristicCrudDataProvider(DocumentService childService) {
        super(childService);
    }
}
