package com.twisted.shell.parser;

public class CommandParser {

    public ParsedCommand parse(String input) {

        String[] split = input.trim().split("\\s+");

        String command = split[0];

        String[] arguments = new String[Math.max(0, split.length - 1)];

        if (split.length > 1) {
            System.arraycopy(split, 1, arguments, 0, arguments.length);
        }

        return new ParsedCommand(command, arguments);

    }

}