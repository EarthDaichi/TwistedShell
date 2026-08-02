package com.twisted.shell.command;

import com.twisted.shell.shell.ShellContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record CommandContext(

        ShellContext shell,

        String rawInput,

        String command,

        List<String> arguments,

        Map<String, String> options,

        Set<String> flags

) {
}