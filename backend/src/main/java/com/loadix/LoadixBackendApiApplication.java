package com.loadix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LoadixBackendApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadixBackendApiApplication.class, args);
    }
}
