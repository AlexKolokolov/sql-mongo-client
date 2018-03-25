package org.kolokolov.testtask;

import org.kolokolov.testtask.commandline.InputProcessor;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private InputProcessor inputProcessor;

    public Application(InputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        inputProcessor.processInput(System.out::println);
    }
}
