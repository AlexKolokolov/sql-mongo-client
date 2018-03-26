package org.kolokolov.testtask.commandline;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Consumer;

@Service
public class InputProcessor {

    private static final String PROMPT = "> ";
    private static final String EXIT_MSG = "bye";

    @Value("${app.exit-command: exit}")
    private String exitCommand;

    private final PrintStream printStream;

    private final Scanner scanner;

    public InputProcessor(InputStream inputStream, PrintStream printStream) {
        this.scanner = new Scanner(inputStream);
        this.printStream = printStream;
    }

    public void processInput(Consumer<String> processLine) {
        String line = promptNextLine();
        while (!exitCommand.equalsIgnoreCase(line)) {
            if (!line.isEmpty()) {
                try {
                    processLine.accept(line);
                } catch (RuntimeException e) {
                    printStream.println(e.getMessage());
                }
            }
            line = promptNextLine();
        }
        printStream.println(EXIT_MSG);
    }

    private String promptNextLine() {
        printStream.print(PROMPT);
        return scanner.nextLine();
    }
}
