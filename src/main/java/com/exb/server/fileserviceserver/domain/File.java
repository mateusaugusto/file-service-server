package com.exb.server.fileserviceserver.domain;

import lombok.Data;

@Data
public class File {

    private String name;
    private java.nio.file.Path path;
}
