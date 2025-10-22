package ir.msob.manak.dms.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.ReplaceOperation;
import ir.msob.jima.core.commons.id.BaseIdService;
import ir.msob.jima.core.commons.shared.timeperiod.TimePeriod;
import ir.msob.manak.core.model.jima.childdomain.characteristic.Characteristic;
import ir.msob.manak.core.model.jima.childdomain.objectvalidation.ObjectValidation;
import ir.msob.manak.core.model.jima.childdomain.relatedaction.RelatedAction;
import ir.msob.manak.core.test.jima.crud.base.domain.DomainCrudDataProvider;
import ir.msob.manak.core.test.shared.ManakCoreTestData;
import ir.msob.manak.domain.model.dms.document.Document;
import ir.msob.manak.domain.model.dms.document.DocumentCriteria;
import ir.msob.manak.domain.model.dms.document.DocumentDto;
import ir.msob.manak.domain.model.dms.document.attachment.Attachment;
import ir.msob.manak.domain.test.shared.ManakDomainTestData;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static ir.msob.jima.core.test.CoreTestData.*;

/**
 * This class provides test data for the {@link Document} class. It extends the {@link DomainCrudDataProvider} class
 * and provides methods to create new test data objects, update existing data objects, and generate JSON patches for updates.
 */
@Component
public class DocumentDataProvider extends DomainCrudDataProvider<Document, DocumentDto, DocumentCriteria, DocumentRepository, DocumentService> {

    private static DocumentDto newDto;
    private static DocumentDto newMandatoryDto;

    private final ObjectMapper objectMapper;

    protected DocumentDataProvider(BaseIdService idService, ObjectMapper objectMapper, DocumentService service, ObjectMapper objectMapper1) {
        super(idService, objectMapper, service);
        this.objectMapper = objectMapper1;
    }

    /**
     * Creates a new DTO object with default values.
     */
    public static void createNewDto() {
        newDto = prepareMandatoryDto();
        newDto.setDescription(DEFAULT_STRING);
        newDto.setKey(DEFAULT_STRING);
        newDto.getTags().add(DEFAULT_STRING);

        // attachment
        newDto.getAttachments().add(ManakDomainTestData.DEFAULT_ATTACHMENT);

        // characteristic
        newDto.getCharacteristics().add(ManakCoreTestData.DEFAULT_CHARACTERISTIC);

        // object validation
        newDto.getObjectValidations().add(ManakCoreTestData.DEFAULT_OBJECT_VALIDATION);

        // related action
        newDto.getRelatedActions().add(ManakCoreTestData.DEFAULT_RELATED_ACTION);
    }

    /**
     * Creates a new DTO object with mandatory fields set.
     */
    public static void createMandatoryNewDto() {
        newMandatoryDto = prepareMandatoryDto();
    }

    /**
     * Creates a new DTO object with mandatory fields set.
     */
    public static DocumentDto prepareMandatoryDto() {
        DocumentDto dto = new DocumentDto();
        dto.setName(DEFAULT_STRING);
        return dto;
    }

    private static void assertValues(DocumentDto dto, String stringValue, Boolean booleanValue, Long longValue, Integer integerValue, TimePeriod timePeriod, Attachment.Status statusValue) {
        Assertions.assertThat(dto.getDescription()).isEqualTo(stringValue);
        Assertions.assertThat(dto.getKey()).isEqualTo(stringValue);
        Assertions.assertThat(dto.getTags()).contains(stringValue);

        // attachments
//        Assertions.assertThat(dto.getAttachments()).hasSameSizeAs(1);
//        for (Attachment beforeAtt : dto.getAttachments()) {
//            Assertions.assertThat(beforeAtt.getFilePath()).isEqualTo(stringValue);
//            Assertions.assertThat(beforeAtt.getFileName()).isEqualTo(stringValue);
//            Assertions.assertThat(beforeAtt.getMimeType()).isEqualTo(stringValue);
//            Assertions.assertThat(beforeAtt.getFileSize()).isEqualTo(longValue);
//            Assertions.assertThat(beforeAtt.getChecksum()).isEqualTo(stringValue);
//            Assertions.assertThat(beforeAtt.getStatus()).isEqualTo(statusValue);
//            Assertions.assertThat(beforeAtt.getOrder()).isEqualTo(integerValue);
//        }

        // characteristics
        Assertions.assertThat(dto.getCharacteristics()).hasSameSizeAs(1);
        for (Characteristic beforeChar : dto.getCharacteristics()) {
            Assertions.assertThat(beforeChar.getKey()).isEqualTo(stringValue);
            Assertions.assertThat(beforeChar.getValue()).isEqualTo(stringValue);
            Assertions.assertThat(beforeChar.getDataType()).isEqualTo(stringValue);
        }

        // object validations
        Assertions.assertThat(dto.getObjectValidations()).hasSameSizeAs(1);
        for (ObjectValidation beforeOv : dto.getObjectValidations()) {
            Assertions.assertThat(beforeOv.getName()).isEqualTo(stringValue);
            Assertions.assertThat(beforeOv.getStatus()).isEqualTo(stringValue);
            Assertions.assertThat(beforeOv.getEnabled()).isEqualTo(booleanValue);
            Assertions.assertThat(beforeOv.getValidFor()).isEqualTo(timePeriod);
        }

        // related actions
        Assertions.assertThat(dto.getRelatedActions()).hasSameSizeAs(1);
        for (RelatedAction beforeRa : dto.getRelatedActions()) {
            Assertions.assertThat(beforeRa.getName()).isEqualTo(stringValue);
            Assertions.assertThat(beforeRa.getStatus()).isEqualTo(stringValue);
            Assertions.assertThat(beforeRa.getMandatory()).isEqualTo(booleanValue);
            Assertions.assertThat(beforeRa.getValidFor()).isEqualTo(timePeriod);
        }
    }

    /**
     *
     */
    @Override
    @SneakyThrows
    public JsonPatch getJsonPatch() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        List<JsonPatchOperation> operations = getMandatoryJsonPatchOperation();
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.description)), new TextNode(UPDATED_STRING)));
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.tags)), factory.arrayNode().add(UPDATED_STRING)));
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.attachments)), factory.arrayNode().add(getObjectMapper().valueToTree(ManakDomainTestData.UPDATED_ATTACHMENT))));
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.characteristics)), factory.arrayNode().add(getObjectMapper().valueToTree(ManakCoreTestData.UPDATED_CHARACTERISTIC))));
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.objectValidations)), factory.arrayNode().add(getObjectMapper().valueToTree(ManakCoreTestData.UPDATED_OBJECT_VALIDATION))));
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.relatedActions)), factory.arrayNode().add(getObjectMapper().valueToTree(ManakCoreTestData.UPDATED_RELATED_ACTION))));
        return new JsonPatch(operations);
    }

    /**
     *
     */
    @Override
    @SneakyThrows
    public JsonPatch getMandatoryJsonPatch() {
        return new JsonPatch(getMandatoryJsonPatchOperation());
    }

    /**
     *
     */
    @Override
    public DocumentDto getNewDto() {
        return newDto;
    }

    /**
     * Updates the given DTO object with the updated value for the domain field.
     *
     * @param dto the DTO object to update
     */
    @Override
    public void updateDto(DocumentDto dto) {
        updateMandatoryDto(dto);
        dto.setDescription(UPDATED_STRING);
        dto.setKey(UPDATED_STRING);
        dto.getTags().clear();
        dto.getTags().add(UPDATED_STRING);

        // attachment
        dto.getAttachments().clear();
        dto.getAttachments().add(ManakDomainTestData.UPDATED_ATTACHMENT);

        // characteristic
        dto.getCharacteristics().clear();
        dto.getCharacteristics().add(ManakCoreTestData.UPDATED_CHARACTERISTIC);

        // object validation
        dto.getObjectValidations().clear();
        dto.getObjectValidations().add(ManakCoreTestData.UPDATED_OBJECT_VALIDATION);

        // related action
        dto.getRelatedActions().clear();
        dto.getRelatedActions().add(ManakCoreTestData.UPDATED_RELATED_ACTION);
    }

    /**
     *
     */
    @Override
    public DocumentDto getMandatoryNewDto() {
        return newMandatoryDto;
    }

    /**
     * Updates the given DTO object with the updated value for the mandatory field.
     *
     * @param dto the DTO object to update
     */
    @Override
    public void updateMandatoryDto(DocumentDto dto) {
        dto.setName(UPDATED_STRING);
    }

    /**
     * Creates a list of JSON patch operations for updating the mandatory field.
     *
     * @return a list of JSON patch operations
     * @throws JsonPointerException if there is an error creating the JSON pointer.
     */
    public List<JsonPatchOperation> getMandatoryJsonPatchOperation() throws JsonPointerException {
        List<JsonPatchOperation> operations = new ArrayList<>();
        operations.add(new ReplaceOperation(new JsonPointer(String.format("/%s", Document.FN.name)), new TextNode(UPDATED_STRING)));
        return operations;
    }

    @Override
    public void assertMandatoryGet(DocumentDto before, DocumentDto after) {
        super.assertMandatoryGet(before, after);
        Assertions.assertThat(after.getName()).isEqualTo(before.getName());
    }

    @Override
    public void assertGet(DocumentDto before, DocumentDto after) {
        super.assertGet(before, after);
        assertMandatoryGet(before, after);

        Assertions.assertThat(after.getDescription()).isEqualTo(before.getDescription());
        Assertions.assertThat(after.getKey()).isEqualTo(before.getKey());
        Assertions.assertThat(after.getTags()).containsExactlyInAnyOrderElementsOf(before.getTags());

        // attachments
        Assertions.assertThat(after.getAttachments()).hasSameSizeAs(before.getAttachments());
        for (Attachment beforeAtt : before.getAttachments()) {
            Attachment afterAtt = after.getAttachments()
                    .stream()
                    .filter(a -> a.getFileName().equals(beforeAtt.getFileName()))
                    .findFirst()
                    .orElseThrow();
            ManakDomainTestData.assertAttachment(beforeAtt, afterAtt);
        }

        // characteristics
        Assertions.assertThat(after.getCharacteristics()).hasSameSizeAs(before.getCharacteristics());
        for (Characteristic beforeChar : before.getCharacteristics()) {
            Characteristic afterChar = after.getCharacteristics()
                    .stream()
                    .filter(c -> c.getKey().equals(beforeChar.getKey()))
                    .findFirst()
                    .orElseThrow();
            ManakCoreTestData.assertCharacteristic(beforeChar, afterChar);
        }

        // object validations
        Assertions.assertThat(after.getObjectValidations()).hasSameSizeAs(before.getObjectValidations());
        for (ObjectValidation beforeOv : before.getObjectValidations()) {
            ObjectValidation afterOv = after.getObjectValidations()
                    .stream()
                    .filter(o -> o.getName().equals(beforeOv.getName()))
                    .findFirst()
                    .orElseThrow();
            ManakCoreTestData.assertObjectValidation(beforeOv, afterOv);
        }

        // related actions
        Assertions.assertThat(after.getRelatedActions()).hasSameSizeAs(before.getRelatedActions());
        for (RelatedAction beforeRa : before.getRelatedActions()) {
            RelatedAction afterRa = after.getRelatedActions()
                    .stream()
                    .filter(r -> r.getName().equals(beforeRa.getName()))
                    .findFirst()
                    .orElseThrow();
            ManakCoreTestData.assertRelatedAction(beforeRa, afterRa);
        }
    }

    @Override
    public void assertMandatoryUpdate(DocumentDto dto, DocumentDto updatedDto) {
        super.assertMandatoryUpdate(dto, updatedDto);
        Assertions.assertThat(dto.getName()).isEqualTo(UPDATED_STRING);
        Assertions.assertThat(updatedDto.getName()).isEqualTo(UPDATED_STRING);
    }

    @Override
    public void assertUpdate(DocumentDto dto, DocumentDto updatedDto) {
        super.assertUpdate(dto, updatedDto);
        assertMandatoryUpdate(dto, updatedDto);
        assertValues(dto, DEFAULT_STRING, DEFAULT_BOOLEAN, DEFAULT_LONG, DEFAULT_INTEGER, DEFAULT_TIME_PERIOD, Attachment.Status.CREATED);
        assertValues(updatedDto, UPDATED_STRING, UPDATED_BOOLEAN, UPDATED_LONG, UPDATED_INTEGER, UPDATED_TIME_PERIOD, Attachment.Status.ACTIVE);
    }

    @Override
    public void assertMandatorySave(DocumentDto dto, DocumentDto savedDto) {
        assertMandatoryGet(dto, savedDto);
    }

    @Override
    public void assertSave(DocumentDto dto, DocumentDto savedDto) {
        super.assertSave(dto, savedDto);
        assertGet(dto, savedDto);
    }


}