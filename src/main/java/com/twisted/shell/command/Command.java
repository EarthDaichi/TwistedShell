package com.twisted.shell.command;

import com.twisted.shell.shell.ShellContext;

public interface Command {


    String name();

    String description();

    String usage();

    void execute(
            ShellContext context,
            String[] args
    );

}