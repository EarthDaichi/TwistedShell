package com.twisted.shell.builtin;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandCategory;
import com.twisted.shell.core.BuildInfo;
import com.twisted.shell.shell.ShellContext;

import java.util.List;

public class VersionCommand implements Command {

    @Override
    public String name() {
        return "version";
    }

    @Override
    public List<String> aliases() {
        return List.of("ver", "v");
    }

    @Override
    public String description() {
        return "Show TwistedShell version.";
    }

    @Override
    public String usage() {
        return "version";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.CORE;
    }

    @Override
    public void execute(ShellContext context, String[] args) {

        context.terminal().println(BuildInfo.name());
        context.terminal().println("Version : " + BuildInfo.version());
        context.terminal().println("Java    : " + BuildInfo.javaVersion());

    }

}