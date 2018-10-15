package com.exb.server.fileserviceserver.controller;

import com.exb.server.fileserviceserver.service.impl.FileServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTestIT {

    @Autowired
    private FileServiceImpl fileService;

    private static String pathLocalFile = "simple_file.txt";

    @Test
    public void should_upload_file() throws Exception {
        MockMultipartFile multipartFile = this.buildMockMultipartFile();
        String created = fileService.saveFile("123", multipartFile);
        assertThat(created).isEqualTo("File created");
    }


    @Test
    public void should_upload_and_open_for_reading_file() throws Exception {
        MockMultipartFile multipartFile = this.buildMockMultipartFile();
        String created = fileService.saveFile("123", multipartFile);
        assertThat(created).isEqualTo("File created");
        InputStream imp = fileService.openForReading("123", "/tmp/" + multipartFile.getOriginalFilename());
        assertThat(imp).isNotNull();
    }

    @Test
    public void should_upload_and_get_parent_file() throws Exception {
        MockMultipartFile multipartFile = this.buildMockMultipartFile();

        String created = fileService.saveFile("123", multipartFile);
        assertThat(created).isEqualTo("File created");
        String parent = fileService.getParent("123", "/tmp/" + multipartFile.getOriginalFilename());
        assertThat(parent).isEqualTo("/tmp");
    }

    @Test
    public void should_upload_and_get_exist_file() throws Exception {
        MockMultipartFile multipartFile = this.buildMockMultipartFile();
        String created = fileService.saveFile("123", multipartFile);
        assertThat(created).isEqualTo("File created");
        Boolean exists = fileService.exists("123", "/tmp/" + multipartFile.getOriginalFilename());
        assertThat(exists).isEqualTo(true);
    }

    private MockMultipartFile buildMockMultipartFile(){
        return  new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());
    }


}
