package org.kolokolov.testtask;

import org.kolokolov.testtask.commandline.InputProcessor;
import org.kolokolov.testtask.converter.SqlToMongoQueryConverter;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Application implements CommandLineRunner {

    private final InputProcessor inputProcessor;
    private final SqlToMongoQueryConverter converter;

    public Application(InputProcessor inputProcessor, SqlToMongoQueryConverter converter) {
        this.inputProcessor = inputProcessor;
        this.converter = converter;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Async
    @Override
    public void run(String... args) {
        inputProcessor.processInput(converter::convertQueryAndRun);
    }
}
