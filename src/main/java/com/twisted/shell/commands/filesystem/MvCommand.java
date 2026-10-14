package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.nio.file.Path;
import java.util.List;

public class MvCommand extends FileCommand{
    @Override
    public String name() {return "mv";}

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Move or rename a file";
    }

    @Override
    public String usage() {
        return "mv <source> <destination>";
    }

    @Override
    protected void run(CommandContext context) throws java.io.IOException {
        requireArgs(context, 2);

        Path source = context.shell().fileSystem().resolvePath(context.arguments().get(0));
        Path destination = context.shell().fileSystem().resolvePath(context.arguments().get(1));

        context.shell().fileSystem().move(source, destination);
        context.shell().terminal().println("File moved.");
    }
}
