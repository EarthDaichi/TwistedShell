package com.twisted.shell.shell;

import com.twisted.shell.command.CommandRegistry;


import java.nio.file.Path;
import java.nio.file.Paths;
import com.twisted.shell.history.CommandHistory;
import com.twisted.shell.terminal.Terminal;

public class ShellContext {

    private final CommandRegistry registry;
    private final Terminal terminal;
    private final CommandHistory history;
    private Path currentDirectory;

    public ShellContext(
            CommandRegistry registry,
            Terminal terminal
    ) {

        this.registry = registry;
        this.terminal = terminal;
        this.history = new CommandHistory();

        this.currentDirectory = Paths.get("").toAbsolutePath();

    }

    public Terminal terminal() {
        return terminal;
    }

    public CommandRegistry registry() {
        return registry;
    }

    public Path currentDirectory() {
        return currentDirectory;
    }

    public void currentDirectory(Path path) {
        this.currentDirectory = path;
    }

    public CommandHistory history() {
        return history;
    }

}