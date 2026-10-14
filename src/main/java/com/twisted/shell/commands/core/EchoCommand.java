package com.twisted.shell.commands.core;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandCategory;
import com.twisted.shell.commandStructure.CommandContext;

import java.util.List;

public class EchoCommand implements Command {

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public List<String> aliases() {
        return List.of("print");
    }

    @Override
    public String description() {
        return "Print text to console";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.CORE;
    }

    @Override
    public String usage() {
        return "echo <text>";
    }

    @Override
    public void execute(CommandContext context) {
        context.println(String.join(" ", context.arguments()));
    }
}