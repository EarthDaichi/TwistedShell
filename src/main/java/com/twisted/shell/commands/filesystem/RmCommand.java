package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.nio.file.Path;
import java.util.List;

public class RmCommand extends FileCommand{
    @Override
    public String name() {return "rm";}

    @Override
    public List<String> aliases() {
        return List.of("delete");
    }

    @Override
    public String description() {
        return "Delete a file or empty directory";
    }

    @Override
    public String usage() {
        return "rm <path>";
    }

    @Override
    protected void run(CommandContext context) throws java.io.IOException{
        requireArgs(context, 1);

        Path path = context.shell().fileSystem().resolvePath(context.arguments().get(0));

        context.shell().fileSystem().delete(path);
        context.shell().terminal().println("Deleted : " + path);
    }
}
