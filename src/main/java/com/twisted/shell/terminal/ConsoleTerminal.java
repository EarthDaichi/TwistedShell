
package com.twisted.shell.terminal;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.io.PrintWriter;

public class ConsoleTerminal implements Terminal, AutoCloseable {

    private final org.jline.terminal.Terminal jlineTerminal;
    private final LineReader lineReader;

    public ConsoleTerminal() {
        try {
            jlineTerminal = org.jline.terminal.TerminalBuilder.builder()
                    .system(true)
                    .build();

            lineReader = LineReaderBuilder.builder()
                    .terminal(jlineTerminal)
                    .build();

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
        PrintWriter writer = jlineTerminal.writer();
        writer.print(text);
        writer.flush();
    }

    @Override
    public void println(String text) {
        PrintWriter writer = jlineTerminal.writer();
        writer.println(text);
        writer.flush();
    }

    public org.jline.terminal.Terminal jlineTerminal() {
        return jlineTerminal;
    }

    @Override
    public void close() throws java.io.IOException {
        jlineTerminal.close();
    }
}
