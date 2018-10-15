package com.exb.server.fileserviceserver.controller;

import com.exb.server.fileserviceserver.service.FileService;
import com.exb.server.fileserviceserver.service.impl.FileServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.webresources.FileResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FileController.class)
public class FileTestIT {


    @Mock
    private FileService fileService;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Spy
    @InjectMocks
    private FileController controller = new FileController();


    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    @WithMockUser(username = "user")
    public void should_create_file() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());

        MvcResult result = mockMvc
                .perform(fileUpload("/file/upload/123")
                        .file(file)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn();

        verify(fileService, times(1)).saveFile("123", file);
        Assert.assertEquals(201, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(username = "user")
    public void should_open_for_reading_file() throws Exception {
        File file = File.createTempFile("hello", ".tmp");
        String pathTmp = file.getAbsolutePath();
        InputStream targetStream = new FileInputStream(file);

        given(fileService.openForReading("123", pathTmp)).willReturn(targetStream);

        MvcResult result = mockMvc
                .perform(get("/file/open/reading/123")
                .param("path", pathTmp))
                .andExpect(status().isOk())
                .andReturn();

        verify(fileService, times(1)).openForReading("123", pathTmp);
        Assert.assertEquals(200, result.getResponse().getStatus());
    }


    @Test
    @WithMockUser(username = "user")
    public void should_list_all_by_session_id_file() throws Exception {
        List<String> listPath = Arrays.asList("/tmp/download.png", "/tmp/download1.png", "/tmp/download2.png", "/tmp/download3.png", "/tmp/download4.png");

        given(fileService.list("123")).willReturn(listPath);

        MvcResult result = mockMvc
                .perform(get("/file/list/123")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        List<String>  actualListPaths = mapper.readValue(result.getResponse().getContentAsString(), List.class);
        verify(fileService, times(1)).list("123");

        Assert.assertEquals(200, result.getResponse().getStatus());
        Assert.assertEquals(listPath.size(), actualListPaths.size());
    }


    @Test
    @WithMockUser(username = "user")
    public void should_return_parent_file() throws Exception {
        String parent = "/tmp";

        given(fileService.getParent("123", "/tmp/download.png")).willReturn(parent);

        MvcResult result = mockMvc.perform(get("/file/parent/123")
                .param("path", "/tmp/download.png")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();


        verify(fileService).getParent("123", "/tmp/download.png");

        Assert.assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(username = "user")
    public void should_not_return_exist_file() throws Exception {
        Boolean fileExist = false;

        given(fileService.exists("123", "/tmp/download.png")).willReturn(fileExist);

        MvcResult result = mockMvc.perform(get("/file/exist/123")
                .param("path", "/tmp/download.png")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        Boolean actualFileExist = mapper.readValue(result.getResponse().getContentAsString(), Boolean.class);

        verify(fileService).exists("123", "/tmp/download.png");
        assertThat(actualFileExist).isEqualTo(fileExist);

    }



}