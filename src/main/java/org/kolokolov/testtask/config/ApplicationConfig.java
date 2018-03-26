package org.kolokolov.testtask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.InputStream;
import java.io.PrintStream;

@Configuration
@EnableAsync
public class ApplicationConfig {

    @Bean
    public InputStream stdIn() {
        return System.in;
    }

    @Bean
    public PrintStream stdOut() {
        return System.out;
    }
}
