package com.twisted.shell.commands.core;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandCategory;
import com.twisted.shell.commandStructure.CommandContext;

import java.util.List;

public class ExitCommand implements Command {

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public List<String> aliases() {
        return List.of("quit","close","end");
    }

    @Override
    public String description() {
        return "Exit TwistedShell.";
    }

    @Override
    public String usage() {
        return "exit";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.CORE;
    }

    @Override
    public void execute(CommandContext context) {

        context.shell().terminal().println("Goodbye!");

        System.exit(0);

    }

}