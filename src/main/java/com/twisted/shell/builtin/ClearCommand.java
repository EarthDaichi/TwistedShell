package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandCategory;
import com.twisted.shell.command.CommandContext;
import com.twisted.shell.util.ConsoleUtils;

import java.util.List;

public class ClearCommand implements Command {

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public List<String> aliases() {
        return List.of("clear");
    }

    @Override
    public String description() {
        return "Clear the console";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.CORE;
    }

    @Override
    public String usage() {
        return "clear";
    }

    @Override
    public void execute(CommandContext context) {
        ConsoleUtils.clear();
    }
}