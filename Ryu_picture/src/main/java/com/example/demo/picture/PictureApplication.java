package com.example.demo.picture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestContextListener;
import springfox.documentation.oas.annotations.EnableOpenApi;


@EnableTransactionManagement
@SpringBootApplication
@EnableOpenApi
@EnableDiscoveryClient
@EnableFeignClients("com.example.demo.commons.feign")
@ComponentScan(basePackages = {
        "com.example.demo.commons.config.feign",
        "com.example.demo.commons.handler",
        "com.example.demo.commons.config.redis",
        "com.example.demo.utils",
        "com.example.demo.picture"})
public class PictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureApplication.class, args);
    }

}
