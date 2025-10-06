package ir.msob.manak.storage.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import ir.msob.jima.core.commons.Constants;
import ir.msob.jima.core.commons.security.BaseTokenService;
import ir.msob.jima.core.commons.security.UserInfoUtil;
import ir.msob.jima.core.commons.util.MultiInputStream;
import ir.msob.jima.core.test.CoreTestData;
import ir.msob.jima.storage.ral.minio.beans.MinioStorageProvider;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.storage.Application;
import ir.msob.manak.storage.ContainerConfiguration;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import static ir.msob.jima.core.test.CoreTestData.DEFAULT_STRING;
import static ir.msob.manak.storage.file.FileDataProvider.*;

@AutoConfigureWebTestClient
@SpringBootTest(classes = {Application.class, ContainerConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CommonsLog
public class FileResourceIT {

    @Autowired
    BaseTokenService tokenService;
    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioStorageProvider minioStorageProvider;
    @Autowired
    FileDataProvider fileDataProvider;
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;


    @SneakyThrows
    @BeforeAll
    public static void beforeAll() {
        CoreTestData.init(new ObjectId().toString(), new ObjectId().toString());
        FileDataProvider.createUploadFiles();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        FileDataProvider.removeUploadFiles();
    }

    @SneakyThrows
    public void prepareAppJsonHeader(HttpHeaders httpHeaders) {
        prepareAppJsonContentHeader(httpHeaders);
        prepareUserInfoHeader(httpHeaders);
        prepareTokenHeader(httpHeaders);
    }

    @SneakyThrows
    public void prepareMultiPartHeader(HttpHeaders httpHeaders) {
        prepareMultiPartContentHeader(httpHeaders);
        prepareUserInfoHeader(httpHeaders);
        prepareTokenHeader(httpHeaders);
    }

    @SneakyThrows
    public void prepareMultiPartContentHeader(HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
    }


    @SneakyThrows
    public void prepareAppJsonContentHeader(HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @SneakyThrows
    public void prepareTokenHeader(HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getToken());
    }

    @SneakyThrows
    public void prepareUserInfoHeader(HttpHeaders httpHeaders) {
        httpHeaders.add(Constants.USER_INFO_HEADER_NAME, UserInfoUtil.encodeUser(objectMapper, userService.<User>getSystemUser()));
    }

    @Test
    @Transactional
    void upload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucketIfNotExists(DEFAULT_RELATED_DOMAIN_TYPE);
        String res = webTestClient.post().uri(String.format("%s/%s/%s/%s/%s/%s", FileResource.URI
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING
                        , DEFAULT_STRING
                        , TEXT_FILE_NAME))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(String.format("%s;charset=UTF-8", MediaType.TEXT_PLAIN_VALUE))
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(res)
                .isNotBlank()
                .startsWith(String.format("%s/%s/%s/%s/"
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING
                        , DEFAULT_STRING))
                .endsWith(TEXT_FILE_NAME);
        boolean exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, res);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    void uploadWithoutRole() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucketIfNotExists(DEFAULT_RELATED_DOMAIN_TYPE);
        String res = webTestClient.post().uri(String.format("%s/%s/%s/%s/%s", FileResource.URI
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING
                        , TEXT_FILE_NAME))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(String.format("%s;charset=UTF-8", MediaType.TEXT_PLAIN_VALUE))
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(res)
                .isNotBlank()
                .startsWith(String.format("%s/%s/%s/"
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING))
                .endsWith(TEXT_FILE_NAME);
        boolean exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, res);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    void uploadWithoutRoleAndreferringType() {
        String res = webTestClient.post().uri(String.format("%s/%s/%s/%s", FileResource.URI
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , CoreTestData.DEFAULT_ID.toString()
                        , TEXT_FILE_NAME))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(String.format("%s;charset=UTF-8", MediaType.TEXT_PLAIN_VALUE))
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(res)
                .isNotBlank()
                .startsWith(String.format("%s/%s/"
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , CoreTestData.DEFAULT_ID.toString()))
                .endsWith(TEXT_FILE_NAME);
        boolean exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, res);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    void uploadWithoutRoleAndReferringTypeAndRelatedId() {
        webTestClient.post().uri(String.format("%s/{entity}/{fileName}", FileResource.URI)
                        , DEFAULT_RELATED_DOMAIN_TYPE
                        , TEXT_FILE_NAME)
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .returnResult(Object.class);

    }

    @Test
    @Transactional
    void uploadWithoutRoleAndReferringTypeAndRelatedIdAndName() {
        webTestClient.post().uri(String.format("%s/{fileName}", FileResource.URI)
                        , TEXT_FILE_NAME)
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .returnResult(Object.class);
    }

    @Test
    @Transactional
    void uploadWithoutRoleAndReferringTypeAndRelatedIdAndNameAndFileName() {
        webTestClient.post().uri(String.format("%s", FileResource.URI))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .returnResult(Object.class);
    }


    @Test
    @Transactional
    void delete() throws IOException, ExecutionException, InterruptedException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucketIfNotExists(DEFAULT_RELATED_DOMAIN_TYPE);
        String path = String.join("/", CoreTestData.DEFAULT_ID.toString(), DEFAULT_STRING, DEFAULT_STRING);
        String filePath = fileService.store(DEFAULT_RELATED_DOMAIN_TYPE, path, TEXT_FILE_NAME, Mono.just(new MultiInputStream(new FileInputStream(TEXT_FILE))), UserService.SYSTEM_USER)
                .toFuture()
                .get();

        boolean exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, filePath);
        Assertions.assertThat(exists).isTrue();

        Boolean res = webTestClient.delete().uri(String.format("%s/%s", FileResource.URI, filePath))
                .headers(this::prepareAppJsonHeader)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(res).isTrue();
        exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, filePath);
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    void get() throws FileNotFoundException, ExecutionException, InterruptedException {
        String path = String.join("/", CoreTestData.DEFAULT_ID.toString(), DEFAULT_STRING, DEFAULT_STRING);

        String filePath = fileService.store(DEFAULT_RELATED_DOMAIN_TYPE, path, TEXT_FILE_NAME, Mono.just(new MultiInputStream(new FileInputStream(TEXT_FILE))), UserService.SYSTEM_USER)
                .toFuture()
                .get();

        boolean exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, filePath);
        Assertions.assertThat(exists).isTrue();

        byte[] res = webTestClient.get().uri(String.format("%s/%s", FileResource.URI, filePath))
                .headers(this::prepareAppJsonHeader)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();

//        Assertions.assertThat(res).isNotNull(); TODO
        exists = minioStorageProvider.exists(DEFAULT_RELATED_DOMAIN_TYPE, filePath);
        Assertions.assertThat(exists).isTrue();
    }

    public void createBucketIfNotExists(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @Test
    @Transactional
    void uploadValidation_invalidExtension() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucketIfNotExists("validation-test");

        webTestClient.post().uri(String.format("%s/%s/%s/%s/%s/%s", FileResource.URI
                        , "validation-test"
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING
                        , DEFAULT_STRING
                        , "file.pdf"))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Transactional
    void uploadValidation_invalidMimeType() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucketIfNotExists("validation-test");

        File invalidFile = Files.createTempFile("invalid", ".txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(invalidFile)) {
            fos.write("%PDF-1.4\n%âãÏÓ\n".getBytes());
        }

        webTestClient.post().uri(String.format("%s/%s/%s/%s/%s/%s", FileResource.URI
                        , "validation-test"
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING
                        , DEFAULT_STRING
                        , "file.txt"))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(invalidFile))
                .exchange()
                .expectStatus().isBadRequest();

        invalidFile.delete();
    }

    @Test
    @Transactional
    void uploadValidation_validFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucketIfNotExists("validation-test");

        String res = webTestClient.post().uri(String.format("%s/%s/%s/%s/%s/%s", FileResource.URI
                        , "validation-test"
                        , CoreTestData.DEFAULT_ID.toString()
                        , DEFAULT_STRING
                        , DEFAULT_STRING
                        , "file.txt"))
                .headers(this::prepareMultiPartHeader)
                .body(fileDataProvider.prepareUploadRequest(TEXT_FILE))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(res).isNotBlank();
    }

}
