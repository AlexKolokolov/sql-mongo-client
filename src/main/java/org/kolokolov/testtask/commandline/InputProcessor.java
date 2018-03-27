package org.kolokolov.testtask.commandline;

import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.function.Consumer;

@Service
public class InputProcessor {

    private static final String PROMPT = "> ";
    private static final String EXIT_MSG = "bye";

    @Value("${app.exit-command:exit}")
    private String exitCommand;

    private final LineReader input;
    private final PrintWriter output;

    public InputProcessor(LineReader input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public void processInput(Consumer<String> processLine) {
        String line = promptNextLine();
        while (!exitCommand.equalsIgnoreCase(line.trim())) {
            if (!line.isEmpty()) {
                try {
                    processLine.accept(line);
                } catch (RuntimeException e) {
                    output.println(e.getMessage());
                }
            }
            line = promptNextLine();
        }
        output.println(EXIT_MSG);
    }

    private String promptNextLine() {
        return input.readLine(PROMPT);
    }
}
