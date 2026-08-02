package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.shell.ShellContext;

public class HelpCommand implements Command {

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