package com.twisted.shell.shell;

import com.twisted.shell.builtin.*;
import com.twisted.shell.builtin.commands.BadAppleCommand;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandContext;
import com.twisted.shell.command.CommandRegistry;
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

        final Terminal terminal = new ConsoleTerminal();

        context = new ShellContext(registry, terminal);

        registry.register(new HelpCommand());
        registry.register(new ExitCommand());
        registry.register(new VersionCommand());
        registry.register(new ClearCommand());
        registry.register(new EchoCommand());
        registry.register(new HistoryCommand());
        registry.register(new BadAppleCommand());
        registry.register(new ColorCommand());
        registry.register(new Playground());

    }

    public void start() {

        while (true) {

            String line = terminal.readLine();

            if (line.isBlank()) {
                continue;
            }

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

            command.execute(commandContext);

        }

    }

}