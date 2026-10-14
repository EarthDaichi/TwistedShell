
package com.twisted.shell.terminal;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConsoleTerminal implements Terminal, AutoCloseable {

    private final org.jline.terminal.Terminal jlineTerminal;
    private final LineReader lineReader;
    private PrintWriter activeWriter;

    public ConsoleTerminal() {
        try {
            jlineTerminal = org.jline.terminal.TerminalBuilder.builder()
                    .system(true)
                    .build();

            lineReader = LineReaderBuilder.builder()
                    .terminal(jlineTerminal)
                    .build();
            activeWriter = jlineTerminal.writer();
        } catch (java.io.IOException e) {
            throw new IllegalStateException(
                    "Cannot initialize JLine terminal.", e
            );
        }
    }

    @Override
    public String readLine() {
        try {
            return lineReader.readLine("twisted> ");
        } catch (org.jline.reader.UserInterruptException e) {
            return "";
        } catch (org.jline.reader.EndOfFileException e) {
            return "exit";
        }
    }

    @Override
    public void print(String text) {
        activeWriter.print(text);
        activeWriter.flush();
    }

    @Override
    public void println(String text) {
        activeWriter.println(text);
        activeWriter.flush();
    }

    public org.jline.terminal.Terminal jlineTerminal() {
        return jlineTerminal;
    }

    @Override
    public void close() throws java.io.IOException {
        jlineTerminal.close();
    }

    public PrintWriter beginOutputRedirect(Path file, boolean append)
            throws IOException {

        PrintWriter previousWriter = activeWriter;

        BufferedWriter writer = append
                ? Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )
                : Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        activeWriter = new PrintWriter(writer);
        return previousWriter;
    }

    public void endOutputRedirect(PrintWriter previousWriter) {
        activeWriter.flush();
        activeWriter.close();
        activeWriter = previousWriter;
    }
}
