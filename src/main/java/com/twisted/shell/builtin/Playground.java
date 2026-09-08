package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandContext;
import com.twisted.shell.command.CommandCategory;

import java.util.List;

public class Playground implements Command {

    @Override
    public String name() {return "playground";}

    @Override
    public String description() {return "for command test";}

    @Override
    public CommandCategory category() {return CommandCategory.UTIL;}

    @Override
    public String usage() {return "PG";}

    @Override
    public List<String> aliases() {return List.of("pg");}

    @Override
    public void execute(CommandContext context) {

    }
}