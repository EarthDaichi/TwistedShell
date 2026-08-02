package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.shell.ShellContext;
import java.util.List;
import com.twisted.shell.command.CommandCategory;

public class HelpCommand implements Command {

    @Override
    public List<String> aliases() {
        return List.of("h");
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.CORE;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Show available commands.";
    }

    @Override
    public String usage() {
        return "help";
    }

    @Override
    public void execute(ShellContext context, String[] args) {

        context.terminal().println("");
        context.terminal().println("Available Commands");
        context.terminal().println("------------------------------");

        for (Command command : context.registry().getCommands()) {

            System.out.printf(
                    "%-15s %s%n",
                    command.name(),
                    command.description()
            );
        }
    }

}