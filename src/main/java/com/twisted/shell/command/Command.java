package com.twisted.shell.command;

import java.util.List;

public interface Command {


    String name();
    List<String> aliases();
    String description();
    String usage();
    CommandCategory category();

    void execute(
            CommandContext context
    );

}