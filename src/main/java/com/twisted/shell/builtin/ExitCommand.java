package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandCategory;
import com.twisted.shell.shell.ShellContext;

import java.util.List;

public class ExitCommand implements Command {

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public List<String> aliases() {
        return List.of("quit");
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
    public void execute(
            ShellContext context,
            String[] args
    ) {

        System.out.println("Goodbye!");

        System.exit(0);

    }

}