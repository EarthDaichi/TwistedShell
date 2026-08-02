package com.twisted.shell.parser;

public record ParsedCommand(
        String command,
        String[] arguments
) {
}