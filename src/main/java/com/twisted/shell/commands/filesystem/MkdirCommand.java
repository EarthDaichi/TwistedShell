package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class MkdirCommand extends FileCommand{
    @Override
    public String name() {return "mkdir";}

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Create directory";
    }

    @Override
    public String usage() {
        return "mkdir <path>";
    }

    @Override
    protected void run(CommandContext context) throws IOException {
        requireArgs(context, 1);

        Path path = context.shell().fileSystem().resolvePath(context.arguments().get(0));

        context.shell().fileSystem().createDirectory(path);

        context.shell().terminal().println("Directory created : " + path);
    }
}
