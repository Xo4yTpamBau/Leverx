package com.sprect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories("com.sprect")
@SpringBootApplication
public class SecurApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurApplication.class, args);
    }
}
