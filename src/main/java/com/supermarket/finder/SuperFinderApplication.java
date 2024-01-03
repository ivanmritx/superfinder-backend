package com.supermarket.finder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableFeignClients
@ComponentScan
public class SuperFinderApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SuperFinderApplication.class, args);
    }

}
