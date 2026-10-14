
package com.twisted.shell.editor;

import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NanoEditor {

    private final Terminal terminal;
    private final Path file;
    private final List<String> lines = new ArrayList<>();

    private int cursorRow = 0;
    private int cursorCol = 0;
    private int topLine = 0;

    private boolean modified = false;
    private boolean running = true;
    private String status = "";

    public NanoEditor(Terminal terminal, Path file) throws IOException {
        this.terminal = terminal;
        this.file = file;

        if (Files.exists(file)) {
            if (!Files.isRegularFile(file)) {
                throw new IOException("Path is not a regular file: " + file);
            }

            String content = Files.readString(file, StandardCharsets.UTF_8)
                    .replace("\r\n", "\n")
                    .replace('\r', '\n');

            lines.addAll(Arrays.asList(content.split("\n", -1)));
        } else {
            lines.add("");
        }
    }

    public void run() throws IOException {
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            terminal.writer().print("\033[?25h");
            terminal.writer().flush();

            while (running) {
                render();

                int key = terminal.reader().read();

                if (key < 0) {
                    break;
                }

                handleKey(key);
            }
        } finally {
            terminal.setAttributes(originalAttributes);

            terminal.writer().print("\033[0m\033[?25h\033[2J\033[H");
            terminal.writer().flush();
        }
    }

    private void handleKey(int key) throws IOException {
        switch (key) {
            case 15 -> save(); // Ctrl+O
            case 24 -> exitEditor(); // Ctrl+X
            case 7 -> status = "Ctrl+O: Save | Ctrl+X: Exit";
            case 13, 10 -> newLine();
            case 8, 127 -> backspace();
            case 27 -> handleEscape();
            default -> {
                if (key >= 32 && key != 127) {
                    insert((char) key);
                }
            }
        }
    }


    private void handleEscape() throws IOException {
        int next = terminal.reader().read(100);

        // Standard ANSI sequence: ESC [ A
        // Application cursor sequence: ESC O A
        if (next == '[' || next == 'O') {
            int key = terminal.reader().read(100);

            switch (key) {
                case 'A' -> moveUp();
                case 'B' -> moveDown();
                case 'C' -> moveRight();
                case 'D' -> moveLeft();
                default -> {
                }
            }
        }
    }


    private void insert(char character) {
        String line = lines.get(cursorRow);

        lines.set(
                cursorRow,
                line.substring(0, cursorCol)
                        + character
                        + line.substring(cursorCol)
        );

        cursorCol++;
        modified = true;
        status = "";
    }

    private void newLine() {
        String line = lines.get(cursorRow);

        String before = line.substring(0, cursorCol);
        String after = line.substring(cursorCol);

        lines.set(cursorRow, before);
        lines.add(cursorRow + 1, after);

        cursorRow++;
        cursorCol = 0;
        modified = true;
        status = "";
    }

    private void backspace() {
        if (cursorCol > 0) {
            String line = lines.get(cursorRow);

            lines.set(
                    cursorRow,
                    line.substring(0, cursorCol - 1)
                            + line.substring(cursorCol)
            );

            cursorCol--;
            modified = true;
        } else if (cursorRow > 0) {
            String previous = lines.get(cursorRow - 1);
            String current = lines.remove(cursorRow);

            cursorRow--;
            cursorCol = previous.length();

            lines.set(cursorRow, previous + current);
            modified = true;
        }

        status = "";
    }

    private void moveUp() {
        if (cursorRow > 0) {
            cursorRow--;
            cursorCol = Math.min(cursorCol, lines.get(cursorRow).length());
        }
    }

    private void moveDown() {
        if (cursorRow < lines.size() - 1) {
            cursorRow++;
            cursorCol = Math.min(cursorCol, lines.get(cursorRow).length());
        }
    }

    private void moveLeft() {
        if (cursorCol > 0) {
            cursorCol--;
        } else if (cursorRow > 0) {
            cursorRow--;
            cursorCol = lines.get(cursorRow).length();
        }
    }

    private void moveRight() {
        if (cursorCol < lines.get(cursorRow).length()) {
            cursorCol++;
        } else if (cursorRow < lines.size() - 1) {
            cursorRow++;
            cursorCol = 0;
        }
    }

    private void save() throws IOException {
        Files.writeString(
                file,
                String.join("\n", lines),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );

        modified = false;
        status = "Saved: " + file;
    }

    private void exitEditor() throws IOException {
        if (!modified) {
            running = false;
            return;
        }

        status = "Save changes? (Y)es / (N)o / (C)ancel";
        render();

        while (true) {
            int key = terminal.reader().read();

            if (key == 'y' || key == 'Y') {
                save();
                running = false;
                return;
            }

            if (key == 'n' || key == 'N') {
                running = false;
                return;
            }

            if (key == 'c' || key == 'C' || key == 27) {
                status = "Exit cancelled";
                return;
            }
        }
    }

    private void render() {
        int width = Math.max(20, terminal.getWidth());
        int height = Math.max(5, terminal.getHeight());
        int bodyRows = Math.max(1, height - 3);

        if (cursorRow < topLine) {
            topLine = cursorRow;
        } else if (cursorRow >= topLine + bodyRows) {
            topLine = cursorRow - bodyRows + 1;
        }

        var writer = terminal.writer();

        writer.print("\033[H\033[2J");
        writer.print("\033[7m nano - " + file + " \033[0m\n");

        for (int i = 0; i < bodyRows; i++) {
            int lineIndex = topLine + i;

            writer.print("\033[2K");

            if (lineIndex < lines.size()) {
                String line = lines.get(lineIndex);
                writer.print(line.substring(0, Math.min(line.length(), width)));
            } else {
                writer.print("~");
            }

            writer.print("\n");
        }

        writer.print("\033[2K");
        writer.print(status.isEmpty()
                ? (modified ? "Modified" : "Ready")
                : status);
        writer.print("\n");

        writer.print("\033[7m ^O Save   ^X Exit   ^G Help \033[0m");

        int screenRow = 2 + cursorRow - topLine;
        int screenCol = Math.min(cursorCol + 1, width);

        writer.print("\033[" + screenRow + ";" + screenCol + "H");
        writer.flush();
    }
}
