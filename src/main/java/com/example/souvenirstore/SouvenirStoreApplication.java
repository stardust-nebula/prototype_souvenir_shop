package com.example.souvenirstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SouvenirStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SouvenirStoreApplication.class, args);
    }

    private JavaMailSender mailSender;
}
