package com.example.souvenirstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class SouvenirStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SouvenirStoreApplication.class, args);
    }

}
