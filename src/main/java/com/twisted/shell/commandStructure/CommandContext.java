
package com.twisted.shell.commandStructure;

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
        Set<String> flags,
        String standardInput,
        StringBuilder outputBuffer
) {

    // Compatibility with existing commands and Shell.java
    public CommandContext(
            ShellContext shell,
            String rawInput,
            String command,
            List<String> arguments,
            Map<String, String> options,
            Set<String> flags
    ) {
        this(
                shell,
                rawInput,
                command,
                arguments,
                options,
                flags,
                null,
                null
        );
    }

    public void print(String text) {
        if (outputBuffer != null) {
            outputBuffer.append(text);
        } else {
            shell.terminal().print(text);
        }
    }

    public void println(String text) {
        if (outputBuffer != null) {
            outputBuffer.append(text).append('\n');
        } else {
            shell.terminal().println(text);
        }
    }

    public boolean hasStandardInput() {
        return standardInput != null;
    }
}
