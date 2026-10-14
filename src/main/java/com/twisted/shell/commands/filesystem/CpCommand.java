package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CpCommand extends FileCommand{
    @Override
    public String name() {return "cp";}

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Copy a file";
    }

    @Override
    public String usage() {
        return "cp <source> <destination>";
    }

    @Override
    protected void run(CommandContext context) throws IOException {
        requireArgs(context, 2);
        Path source = context.shell().fileSystem().resolvePath(context.arguments().get(0));
        Path destination = context.shell().fileSystem().resolvePath(context.arguments().get(1));

        context.shell().fileSystem().copy(source, destination);
        context.shell().terminal().println("File Copied");
    }
}
