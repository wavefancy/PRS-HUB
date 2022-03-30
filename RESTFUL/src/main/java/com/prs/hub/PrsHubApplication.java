package com.prs.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class PrsHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrsHubApplication.class, args);
    }

}
