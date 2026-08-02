package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandCategory;
import com.twisted.shell.command.CommandContext;

import java.util.List;

public class HistoryCommand implements Command {

    @Override
    public String name() {
        return "history";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Show command history";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.CORE;
    }

    @Override
    public String usage() {
        return "history";
    }

    @Override
    public void execute(CommandContext context) {

        List<String> history = context.shell().history().getAll();

        for (int i = 0; i < history.size(); i++) {
            System.out.println(
                    (i + 1) + " " + history.get(i)
            );
        }
    }
}