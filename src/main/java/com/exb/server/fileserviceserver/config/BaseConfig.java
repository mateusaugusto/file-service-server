package com.exb.server.fileserviceserver.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan({})
public class BaseConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
