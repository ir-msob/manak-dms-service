package ir.msob.manak.storage.file;

import ir.msob.jima.core.commons.id.BaseIdService;
import ir.msob.jima.storage.commons.BaseStorageService;
import ir.msob.jima.storage.commons.FileValidator;
import ir.msob.jima.storage.ral.minio.beans.MinioStorageProvider;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.service.IdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService implements BaseStorageService<String, User, MinioStorageProvider> {
    private final MinioStorageProvider minioStorageProvider;
    private final IdService idService;
    private final FileValidator fileValidator;


    @Override
    public MinioStorageProvider getStorageProvider() {
        return minioStorageProvider;
    }

    @Override
    public FileValidator getFileValidator() {
        return fileValidator;
    }

    @Override
    public BaseIdService getIdService() {
        return idService;
    }

}
