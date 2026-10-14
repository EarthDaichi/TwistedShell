
package com.twisted.shell.commands.filesystem;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandContext;
import com.twisted.shell.commandStructure.CommandCategory;

import java.io.IOException;
import java.util.List;

abstract class FileCommand implements Command {

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.FILESYSTEM;
    }

    @Override
    public final void execute(CommandContext context) {
        try {
            run(context);
        } catch (IOException | SecurityException
                 | IllegalArgumentException e) {
            context.shell().terminal().println(
                    "Error: " + e.getMessage()
            );
        }
    }

    protected abstract void run(CommandContext context)
            throws IOException;

    protected void requireArgs(
            CommandContext context,
            int count
    ) {
        if (context.arguments().size() != count) {
            throw new IllegalArgumentException(
                    "Usage: " + usage()
            );
        }
    }

    protected void requireArgsBetween(
            CommandContext context,
            int min,
            int max
    ) {
        int count = context.arguments().size();

        if (count < min || count > max) {
            throw new IllegalArgumentException(
                    "Usage: " + usage()
            );
        }
    }
}
