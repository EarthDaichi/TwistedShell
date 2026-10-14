package com.twisted.shell.commands.core;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandCategory;
import com.twisted.shell.commandStructure.CommandContext;
import com.twisted.shell.util.ConsoleUtils;

import java.util.List;

public class ClearCommand implements Command {

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public List<String> aliases() {
        return List.of();
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