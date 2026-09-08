package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandCategory;
import com.twisted.shell.command.CommandContext;

import java.util.List;

public class ColorCommand implements Command {

    @Override
    public String name() {return "color"; }

    @Override
    public String description() {return "Change color of text and Background";}

    @Override
    public CommandCategory category() {return CommandCategory.CORE;}

    @Override
    public String usage() {return "color";}

    @Override
    public List<String> aliases() {return List.of();}

    @Override
    public void execute(CommandContext context) {
        System.out.print("\033[32m");
    }
}