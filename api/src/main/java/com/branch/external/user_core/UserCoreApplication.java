package com.branch.external.user_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class UserCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCoreApplication.class, args);
    }

}
