package com.ld.poetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class UtopianApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtopianApplication.class, args);
    }

}
