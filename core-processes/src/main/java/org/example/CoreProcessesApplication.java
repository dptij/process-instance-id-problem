package org.example;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("core-processes")
public class CoreProcessesApplication {
    public static void main(String... args) {
        SpringApplication.run(CoreProcessesApplication.class, args);
    }
}
