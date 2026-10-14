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
        return List.of();
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
        if (context.arguments().isEmpty() && context.hasStandardInput()) {
            context.print(context.standardInput());
            return;
        }

        if (context.arguments().isEmpty()) {
            throw new IllegalArgumentException("Usage: cat <path>");
        }

        Path path = context.shell().fileSystem()
                .resolvePath(context.arguments().get(0));

        context.print(
                context.shell().fileSystem().readText(path)
        );
    }
}
