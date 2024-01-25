package com.fiap.techchallenge.followup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FollowupApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowupApplication.class, args);
    }

}
