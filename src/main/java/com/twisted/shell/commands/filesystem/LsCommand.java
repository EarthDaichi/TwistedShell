package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LsCommand extends FileCommand{
    @Override
    public String name() {return "ls";}

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "List files and directories";
    }

    @Override
    public String usage() {
        return "ls [path]";
    }

    @Override
    protected void run(CommandContext context) throws java.io.IOException {
        requireArgsBetween(context, 0, 1);

        Path path = context.arguments().isEmpty()
                ? context.shell().currentDirectory()
                : context.shell().fileSystem().resolvePath(context.arguments().get(0));
        for (Path entry : context.shell().fileSystem().list(path)) {
            String name = entry.getFileName() == null
                    ? entry.toString()
                    : entry.getFileName().toString();

            if (Files.isDirectory(entry)) {
                name += "/";
            }

            context.shell().terminal().println(name);
        }

    }
}
