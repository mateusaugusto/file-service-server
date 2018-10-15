package com.exb.server.fileserviceserver.service.impl;

import DTO.FileDTO;
import com.exb.server.fileserviceserver.domain.File;
import com.exb.server.fileserviceserver.exception.FileServiceException;
import com.exb.server.fileserviceserver.repository.FileRepository;
import com.exb.server.fileserviceserver.service.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public String saveFile(final String aSessionId, MultipartFile file) throws IOException {
        fileRepository.save(aSessionId, file);
        return "File created";
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
    public List<com.exb.server.fileserviceserver.domain.File> findAllFile(final String aSessionId) throws FileServiceException {
        return this.fileRepository.findAllFile(aSessionId);
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

    @Override
    public List<FileDTO> convertListFileToListFileDto(List<File> fileList){
        List<FileDTO> baseFileDTOList = new ArrayList<>();
        fileList.forEach(campaign -> baseFileDTOList.add(this.convert(campaign)));
        return baseFileDTOList;
    }

    public FileDTO convert(File file) {

        if (null == file) {
            return null;
        }

        FileDTO fileDTO = this.modelMapper.map(file, FileDTO.class);

        return fileDTO;
    }
}
