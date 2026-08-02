package com.twisted.shell.command;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {

        commands.put(command.name(), command);

        for (String alias : command.aliases()) {
            commands.put(alias, command)
        }
    }

    public Command find(String name) {
        return commands.get(name);
    }

    public Collection<Command> getCommands() {

        return new LinkedHashSet<>(commands.values());

    }

}