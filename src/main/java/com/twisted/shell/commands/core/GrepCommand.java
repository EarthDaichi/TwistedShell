
package com.twisted.shell.commands.core;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandCategory;
import com.twisted.shell.commandStructure.CommandContext;

import java.util.List;

public class GrepCommand implements Command {

    @Override
    public String name() {
        return "grep";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Search for matching lines in input";
    }

    @Override
    public String usage() {
        return "grep <pattern>";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.UTIL;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.arguments().size() != 1) {
            context.println("Usage: " + usage());
            return;
        }

        String input = context.standardInput();

        if (input == null) {
            context.println("grep: no input provided");
            return;
        }

        String pattern = context.arguments().get(0);

        for (String line : input.split("\\R", -1)) {
            if (line.contains(pattern)) {
                context.println(line);
            }
        }
    }
}
