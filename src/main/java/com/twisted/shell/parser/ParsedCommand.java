
package com.twisted.shell.parser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ParsedCommand(
        String command,
        List<String> arguments,
        Map<String, String> options,
        Set<String> flags,
        String inputFile,
        String outputFile,
        boolean appendOutput
) {
    // รองรับโค้ดเดิมที่ใช้ Constructor 4 arguments
    public ParsedCommand(
            String command,
            List<String> arguments,
            Map<String, String> options,
            Set<String> flags
    ) {
        this(command, arguments, options, flags, null, null, false);
    }
}
