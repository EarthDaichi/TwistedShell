
package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandCategory;
import com.twisted.shell.commandStructure.CommandContext;
import com.twisted.shell.editor.NanoEditor;
import com.twisted.shell.terminal.ConsoleTerminal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class NanoCommand implements Command {

    @Override
    public String name() {
        return "nano";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String description() {
        return "Open a file in the terminal text editor";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.FILESYSTEM;
    }

    @Override
    public String usage() {
        return "nano <file>";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.arguments().isEmpty()) {
            context.shell().terminal().println("Usage: " + usage());
            return;
        }

        if (!(context.shell().terminal() instanceof ConsoleTerminal console)) {
            context.shell().terminal().println(
                    "Error: nano requires ConsoleTerminal with JLine."
            );
            return;
        }

        Path file = context.shell().currentDirectory()
                .resolve(context.arguments().get(0))
                .normalize();

        try {
            new NanoEditor(console.jlineTerminal(), file).run();
        } catch (IOException e) {
            context.shell().terminal().println(
                    "nano: " + e.getMessage()
            );
        }
    }
}
