package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.util.List;

public class PwdCommand extends FileCommand {
    @Override
    public String name() {return "pwd";}

    @Override
    public List<String> aliases() {
        return List.of("dir");
    }

    @Override
    public String description() {
        return "Print working directory";
    }

    @Override
    public String usage() {
        return "pwd";
    }

    @Override
    protected void run(CommandContext context) {
        requireArgs(context, 0);

        context.shell().terminal().println(
                context.shell().currentDirectory().toString()
        );
    }
}
