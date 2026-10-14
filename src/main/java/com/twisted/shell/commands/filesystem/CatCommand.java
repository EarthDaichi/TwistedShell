package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.nio.file.Path;
import java.util.List;

public class CatCommand extends FileCommand{
    @Override
    public String name() {
        return "cat";
    }

    @Override
    public List<String> aliases() {
        return List.of("nano");
    }

    @Override
    public String description() {
        return "Display a text file";
    }

    @Override
    public String usage() {
        return "cat <path>";
    }

    @Override
    protected void run(CommandContext context) throws java.io.IOException {
        requireArgs(context, 1);

        Path path = context.shell().fileSystem().resolvePath(context.arguments().get(0));

        context.shell().terminal().println(context.shell().fileSystem().readText(path));
    }
}
