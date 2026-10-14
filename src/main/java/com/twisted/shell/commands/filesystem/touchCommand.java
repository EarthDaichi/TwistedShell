package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.CommandContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class touchCommand extends FileCommand{
    @Override
    public String name() {return "touch";}

    @Override
    public List<String> aliases() {return List.of("mkfile");}

    @Override
    public String description() {return "Make an empty file";}

    @Override
    public String usage() {return "touch <filename>";}

    @Override
    protected void run(CommandContext context) {
        requireArgs(context, 1);

        Path file = context.shell().currentDirectory().resolve(context.arguments().get(0)).normalize();

        try {
            if (Files.exists(file)) {
                context.shell().terminal().println("File " + file + " already exists.");
                return;
            }
            Files.createFile(file);
            context.shell().terminal().println("File " + file + " created.");
        }
        catch (IOException e) {
            context.shell().terminal().println("Cannot create file " + e.getMessage());
        }

    }

}
