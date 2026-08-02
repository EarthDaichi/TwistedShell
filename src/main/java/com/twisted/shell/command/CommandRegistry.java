package com.twisted.shell.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        commands.put(command.name(), command);
    }

    public Command find(String name) {
        return commands.get(name);
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

}