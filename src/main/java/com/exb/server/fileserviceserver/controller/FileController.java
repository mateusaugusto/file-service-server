package com.exb.server.fileserviceserver.controller;

import DTO.FileDTO;
import com.exb.server.fileserviceserver.domain.File;
import com.exb.server.fileserviceserver.service.FileService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;


    @PostMapping(value = "/upload/{aSessionId}", headers = "content-type=multipart/*")
    public ResponseEntity<String> upload(@PathVariable("aSessionId") String aSessionId,
                                                 @RequestParam("file") MultipartFile file) throws IOException {
        String result = fileService.saveFile(aSessionId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping(value = "/exist/{aSessionId}")
    public ResponseEntity<Boolean> getExist(@NonNull @PathVariable("aSessionId") String aSessionId,
                                                    @NonNull @RequestParam("path") String path) throws IOException {
        boolean existFile = fileService.exists(aSessionId, path);
        return ResponseEntity.status(HttpStatus.OK).body(existFile);
    }

    @GetMapping(value = "/parent/{aSessionId}")
    public ResponseEntity<String> getParent(@NonNull @PathVariable("aSessionId") String aSessionId,
                                            @NonNull @RequestParam("path") String path) throws IOException {

        String parentPath = fileService.getParent(aSessionId, path);
        return ResponseEntity.status(HttpStatus.OK).body(parentPath);
    }

    @DeleteMapping(value = "/{aSessionId}")
    public ResponseEntity<Void> delete(@NonNull @PathVariable("aSessionId") String aSessionId,
                                          @NonNull @RequestParam("path") String path,
                                          @NonNull @RequestParam("recursive") boolean recursive) throws IOException {

        fileService.delete(aSessionId, path, recursive);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/list/{aSessionId}")
    public ResponseEntity<List<String>> findAll(@NonNull @PathVariable("aSessionId") String aSessionId) throws IOException {
        List<String> listPath = fileService.list(aSessionId);
        return ResponseEntity.status(HttpStatus.OK).body(listPath);
    }

    @GetMapping(value = "/all/{aSessionId}")
    public ResponseEntity<List<FileDTO>> findAllFile(@NonNull @PathVariable("aSessionId") String aSessionId) throws IOException {
        List<File> listFile = fileService.findAllFile(aSessionId);
        List<FileDTO> listFileDTO  = fileService.convertListFileToListFileDto(listFile);
        return ResponseEntity.status(HttpStatus.OK).body(listFileDTO);
    }

    @GetMapping(value = "/open/writing/{aSessionId}")
    public ResponseEntity<Void> openForWriting(@NonNull @PathVariable("aSessionId") String aSessionId,
                                               @NonNull @RequestParam("path") String path,
                                               @NonNull @RequestParam("append") boolean append) throws IOException {

        fileService.openForWriting(aSessionId, path, append);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/open/reading/{aSessionId}")
    public ResponseEntity<byte[]> openForReading(@NonNull @PathVariable("aSessionId") String aSessionId,
                                                 @NonNull @RequestParam("path") String path) throws IOException {
        InputStream in = fileService.openForReading(aSessionId, path);
        return ResponseEntity.status(HttpStatus.OK).body(IOUtils.toByteArray(in));
    }


}
