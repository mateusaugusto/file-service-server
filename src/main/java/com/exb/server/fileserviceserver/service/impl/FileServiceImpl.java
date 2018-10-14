package com.exb.server.fileserviceserver.service.impl;

import com.exb.server.fileserviceserver.exception.FileServiceException;
import com.exb.server.fileserviceserver.repository.FileRepository;
import com.exb.server.fileserviceserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public String saveFile(final String aSessionId, MultipartFile file) throws IOException {
        fileRepository.save(aSessionId, file);
        return "done";
    }

    @Override
    public OutputStream openForWriting(final String aSessionId, final String aPath, final boolean aAppend) throws FileServiceException {
        return this.fileRepository.openForWriting(aSessionId, aPath, aAppend);
    }

    @Override
    public InputStream openForReading(final String aSessionId, final String aPath) throws FileServiceException {
      return this.fileRepository.openForReading(aSessionId, aPath);
    }

    @Override
    public List<String> list(final String aSessionId) throws FileServiceException {
       return this.fileRepository.findAll(aSessionId);
    }

    @Override
    public void delete(final String aSessionId, final String aPath, final boolean aRecursive)
            throws FileServiceException {
        this.fileRepository.delete(aSessionId, aPath, aRecursive);

    }

    @Override
    public boolean exists(final String aSessionId, final String aPath) throws FileServiceException {
        return this.fileRepository.exists(aSessionId, aPath);
    }

    @Override
    public String getParent(final String aSessionId, final String aPath) throws FileServiceException {
        return this.fileRepository.getParent(aSessionId, aPath);
    }
}
