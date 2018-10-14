package com.exb.server.fileserviceserver.service;

import DTO.FileDTO;
import com.exb.server.fileserviceserver.domain.File;
import com.exb.server.fileserviceserver.exception.FileServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.validation.constraints.NotNull;

public interface FileService {

    String saveFile(final String aSessionId, MultipartFile file) throws IOException;

    OutputStream openForWriting(@NotNull final String aSessionId, @NotNull final String aPath,
                                final boolean aAppend)
            throws FileServiceException;

    InputStream openForReading(@NotNull final String aSessionId, @NotNull final String aPath)
            throws FileServiceException;

    List<String> list(@NotNull final String aSessionId)
            throws FileServiceException;

    List<com.exb.server.fileserviceserver.domain.File> findAllFile(String aSessionId) throws FileServiceException;

    void delete(@NotNull final String aSessionId, @NotNull final String aPath, final boolean aRecursive)
            throws FileServiceException;

    boolean exists(@NotNull final String aSessionId, @NotNull final String aPath)
            throws FileServiceException;

    String getParent(@NotNull final String aSessionId, @NotNull final String aPath)
            throws FileServiceException;

    List<FileDTO> convertListFileToListFileDto(List<File> fileList);
}
