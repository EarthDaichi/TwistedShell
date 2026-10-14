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