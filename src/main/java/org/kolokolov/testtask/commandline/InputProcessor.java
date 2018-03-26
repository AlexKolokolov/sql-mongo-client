package org.kolokolov.testtask.commandline;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class InputProcessor {

    private static final String PROMPT = "> ";
    private static final String EXIT_MSG = "bye";

    @Value("${app.exit-command: exit}")
    private String exitCommand;

    private final Terminal terminal;

    private final LineReader lineReader;

    public InputProcessor(Terminal terminal) {
        this.terminal = terminal;
        this.lineReader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    public void processInput(Consumer<String> processLine) {
        String line = promptNextLine();
        while (!exitCommand.equalsIgnoreCase(line)) {
            if (!line.isEmpty()) {
                try {
                    processLine.accept(line);
                } catch (RuntimeException e) {
                    terminal.writer().println(e.getMessage());
                }
            }
            line = promptNextLine();
        }
        terminal.writer().println(EXIT_MSG);
    }

    private String promptNextLine() {
        return lineReader.readLine(PROMPT);
    }
}
