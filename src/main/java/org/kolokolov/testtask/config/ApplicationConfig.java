package org.kolokolov.testtask.config;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableAsync
public class ApplicationConfig {

    @Bean
    public PrintWriter stdOut() throws IOException {
        return terminal().writer();
    }

    @Bean
    public LineReader stdIn() throws IOException {
        return LineReaderBuilder.builder().terminal(terminal()).build();
    }

    @Bean
    public Terminal terminal() throws IOException {
        return TerminalBuilder.builder().system(true).build();
    }
}
