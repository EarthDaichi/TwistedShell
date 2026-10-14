package com.twisted.shell.parser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandParser {


    public ParsedCommand parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        List<String> tokens = new java.util.ArrayList<>();
        StringBuilder token = new StringBuilder();

        char quote = 0;
        boolean tokenStarted = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (quote != 0) {
                if (c == quote) {
                    quote = 0;
                } else {
                    token.append(c);
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                quote = c;
                tokenStarted = true;
            } else if (Character.isWhitespace(c)) {
                if (tokenStarted) {
                    tokens.add(token.toString());
                    token.setLength(0);
                    tokenStarted = false;
                }
            } else {
                token.append(c);
                tokenStarted = true;
            }
        }

        if (quote != 0) {
            throw new IllegalArgumentException(
                    "Unclosed quotation mark."
            );
        }

        if (tokenStarted) {
            tokens.add(token.toString());
        }

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        return new ParsedCommand(
                tokens.get(0),
                tokens.subList(1, tokens.size()),
                java.util.Map.of(),
                java.util.Set.of()
        );
    }


}