package ir.msob.manak.storage.file;

import ir.msob.jima.core.commons.resource.Resource;
import ir.msob.jima.core.commons.security.BaseUserService;
import ir.msob.jima.core.commons.shared.ResourceType;
import ir.msob.jima.storage.api.restful.service.rest.BaseStorageRestResource;
import ir.msob.jima.storage.ral.minio.beans.MinioStorageProvider;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FileResource.URI)
@RequiredArgsConstructor
@Resource(value = "file", type = ResourceType.RESTFUL)
public class FileResource implements BaseStorageRestResource<String, User, MinioStorageProvider, FileService> {
    public static final String URI = "/api/v1/file";
    private final FileService fileService;
    private final UserService userService;

    @Override
    public FileService getService() {
        return fileService;
    }

    @Override
    public BaseUserService getUserService() {
        return userService;
    }

    @Override
    public String getStorageUri() {
        return URI;
    }

}
