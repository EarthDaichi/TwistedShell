package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class CdCommand extends FileCommand{
    @Override
    public String name() {return "cd";}

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Change working directory";
    }

    @Override
    public String usage() {
        return "cd [path]";
    }

    @Override
    protected void run(CommandContext context) {
        requireArgsBetween(context, 0, 1);

        String input = context.arguments().isEmpty()
                ? System.getProperty("user.home")
                : context.arguments().get(0);

        Path path = context.shell().fileSystem().resolvePath(input);

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException(
                    "Directory not found : " + path
            );
        }
        context.shell().currentDirectory(path);
    }
}
