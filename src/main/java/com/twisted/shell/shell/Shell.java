package com.twisted.shell.shell;

import com.twisted.shell.commands.core.*;
import com.twisted.shell.commands.meme.*;
import com.twisted.shell.commands.filesystem.*;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandContext;
import com.twisted.shell.commandStructure.CommandRegistry;
import com.twisted.shell.parser.CommandParser;
import com.twisted.shell.parser.ParsedCommand;
import com.twisted.shell.terminal.ConsoleTerminal;
import com.twisted.shell.terminal.Terminal;

public class Shell {

    private final CommandRegistry registry = new CommandRegistry();
    private final ShellContext context;
    private final CommandParser parser = new CommandParser();
    private final Terminal terminal = new ConsoleTerminal();

    public Shell() {
        context = new ShellContext(registry, terminal);

        registry.register(new HelpCommand());
        registry.register(new ExitCommand());
        registry.register(new VersionCommand());
        registry.register(new ClearCommand());
        registry.register(new EchoCommand());
        registry.register(new HistoryCommand());
        registry.register(new BadAppleCommand());
        registry.register(new ColorCommand());
        registry.register(new LsCommand());
        registry.register(new CdCommand());
        registry.register(new PwdCommand());
        registry.register(new MkdirCommand());
        registry.register(new RmCommand());
        registry.register(new CpCommand());
        registry.register(new MvCommand());
        registry.register(new CatCommand());
        registry.register(new touchCommand());
        registry.register(new NanoCommand());
        registry.register(new GrepCommand());
    }

    public void start() {
        while (true) {
            String line = terminal.readLine();

            if (line == null || line.isBlank()) {
                continue;
            }

            try {
                java.util.List<String> segments = parser.splitPipeline(line);

                if (segments.size() == 1) {
                    ParsedCommand parsed = parser.parse(line);
                    Command command = registry.find(parsed.command());

                    if (command == null) {
                        terminal.println("Unknown command.");
                        continue;
                    }

                    CommandContext commandContext = new CommandContext(
                            context,
                            line,
                            parsed.command(),
                            parsed.arguments(),
                            parsed.options(),
                            parsed.flags()
                    );

                    context.history().add(line);
                    executeWithRedirection(command, commandContext, parsed);

                } else {
                    context.history().add(line);
                    executePipeline(line, segments);
                }

            } catch (IllegalArgumentException e) {
                terminal.println("Parse error: " + e.getMessage());
            }
        }
    }



    private void executeWithRedirection(
            Command command,
            CommandContext commandContext,
            ParsedCommand parsed
    ) {
        CommandContext effectiveContext = commandContext;



        if (parsed.inputFile() != null) {
            java.nio.file.Path inputPath = context.currentDirectory()
                    .resolve(parsed.inputFile())
                    .normalize();

            try {
                String input = java.nio.file.Files.readString(
                        inputPath,
                        java.nio.charset.StandardCharsets.UTF_8
                );

                effectiveContext = new CommandContext(
                        context,
                        commandContext.rawInput(),
                        commandContext.command(),
                        commandContext.arguments(),
                        commandContext.options(),
                        commandContext.flags(),
                        input,
                        null
                );

            } catch (java.io.IOException e) {
                terminal.println("Input redirection error: " + e.getMessage());
                return;
            }
        }



        if (parsed.outputFile() == null) {
            command.execute(effectiveContext);
            return;
        }

        if (!(terminal instanceof ConsoleTerminal console)) {
            terminal.println("Redirection requires ConsoleTerminal.");
            return;
        }

        java.nio.file.Path file = context.currentDirectory()
                .resolve(parsed.outputFile())
                .normalize();

        java.io.PrintWriter previousWriter = null;
        boolean redirected = false;

        try {
            previousWriter = console.beginOutputRedirect(
                    file,
                    parsed.appendOutput()
            );
            redirected = true;

            command.execute(effectiveContext);

        } catch (java.io.IOException e) {
            terminal.println("Redirection error: " + e.getMessage());

        } finally {
            if (redirected) {
                console.endOutputRedirect(previousWriter);
            }
        }
    }


    private void executePipeline(
            String rawInput,
            java.util.List<String> segments
    ) {
        java.util.List<ParsedCommand> commands = new java.util.ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            ParsedCommand parsed = parser.parse(segments.get(i));

            Command command = registry.find(parsed.command());

            if (command == null) {
                terminal.println("Unknown command: " + parsed.command());
                return;
            }

            if (parsed.inputFile() != null && i != 0) {
                terminal.println(
                        "Input redirection is only allowed on the first command."
                );
                return;
            }

            if (parsed.outputFile() != null && i != segments.size() - 1) {
                terminal.println(
                        "Output redirection is only allowed on the last command."
                );
                return;
            }

            commands.add(parsed);
        }

        String standardInput = null;

        ParsedCommand firstCommand = commands.get(0);

        if (firstCommand.inputFile() != null) {
            java.nio.file.Path inputPath = context.currentDirectory()
                    .resolve(firstCommand.inputFile())
                    .normalize();

            try {
                standardInput = java.nio.file.Files.readString(
                        inputPath,
                        java.nio.charset.StandardCharsets.UTF_8
                );
            } catch (java.io.IOException e) {
                terminal.println("Input redirection error: " + e.getMessage());
                return;
            }
        }

        for (int i = 0; i < commands.size(); i++) {
            ParsedCommand parsed = commands.get(i);
            Command command = registry.find(parsed.command());

            StringBuilder outputBuffer = new StringBuilder();

            CommandContext commandContext = new CommandContext(
                    context,
                    rawInput,
                    parsed.command(),
                    parsed.arguments(),
                    parsed.options(),
                    parsed.flags(),
                    standardInput,
                    outputBuffer
            );

            command.execute(commandContext);

            standardInput = outputBuffer.toString();
        }

        ParsedCommand lastCommand = commands.get(commands.size() - 1);
        String output = standardInput == null ? "" : standardInput;

        if (lastCommand.outputFile() == null) {
            terminal.print(output);
            return;
        }

        java.nio.file.Path file = context.currentDirectory()
                .resolve(lastCommand.outputFile())
                .normalize();

        try {
            if (lastCommand.appendOutput()) {
                java.nio.file.Files.writeString(
                        file,
                        output,
                        java.nio.charset.StandardCharsets.UTF_8,
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND
                );
            } else {
                java.nio.file.Files.writeString(
                        file,
                        output,
                        java.nio.charset.StandardCharsets.UTF_8,
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                        java.nio.file.StandardOpenOption.WRITE
                );
            }
        } catch (java.io.IOException e) {
            terminal.println("Redirection error: " + e.getMessage());
        }
    }

}