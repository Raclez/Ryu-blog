package com.example.demo.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author RL475
 */
@SpringBootApplication
public class SsoApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(SsoApplication.class, args);
    }

}
