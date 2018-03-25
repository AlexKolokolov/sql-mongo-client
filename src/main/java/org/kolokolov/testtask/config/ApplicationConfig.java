package org.kolokolov.testtask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.PrintStream;

@Configuration
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
