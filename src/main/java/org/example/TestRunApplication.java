package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Optional;

@SpringBootApplication
@Slf4j
public class TestRunApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment environment = SpringApplication.run(TestRunApplication.class, args).getEnvironment();
        log.info("application start succeed");
        log.info("http://localhost:{}{}/hello",environment.getProperty("server.port"),
                Optional.ofNullable(environment.getProperty("server.servlet.context-path")).orElse(""));
    }
}
