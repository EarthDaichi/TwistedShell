package com.twisted.shell.command;

import com.twisted.shell.shell.ShellContext;
import java.util.List;

public interface Command {


    String name();
    List<String> aliases();
    String description();
    String usage();
    CommandCategory category();

    void execute(
            ShellContext context,
            String[] args
    );

}