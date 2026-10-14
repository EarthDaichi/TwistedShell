
package com.twisted.shell.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandParser {

    private record Lexeme(String value, boolean operator) {}

    public ParsedCommand parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        List<Lexeme> tokens = new ArrayList<>();
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
            } else if (c == '>' || c == '<') {
                if (tokenStarted) {
                    tokens.add(new Lexeme(token.toString(), false));
                    token.setLength(0);
                    tokenStarted = false;
                }

                if (c == '>' && i + 1 < input.length()
                        && input.charAt(i + 1) == '>') {
                    tokens.add(new Lexeme(">>", true));
                    i++;
                } else {
                    tokens.add(new Lexeme(String.valueOf(c), true));
                }
            } else if (Character.isWhitespace(c)) {
                if (tokenStarted) {
                    tokens.add(new Lexeme(token.toString(), false));
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
            tokens.add(new Lexeme(token.toString(), false));
        }

        if (tokens.isEmpty() || tokens.get(0).operator()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        String command = tokens.get(0).value();
        List<String> arguments = new ArrayList<>();

        String inputFile = null;
        String outputFile = null;
        boolean appendOutput = false;

        for (int i = 1; i < tokens.size(); i++) {
            Lexeme current = tokens.get(i);

            if (!current.operator()) {
                arguments.add(current.value());
                continue;
            }

            String operator = current.value();

            if (i + 1 >= tokens.size()
                    || tokens.get(i + 1).operator()) {
                throw new IllegalArgumentException(
                        "Missing filename after " + operator
                );
            }

            String filename = tokens.get(++i).value();

            if (operator.equals("<")) {
                if (inputFile != null) {
                    throw new IllegalArgumentException(
                            "Multiple input redirections are not supported."
                    );
                }

                inputFile = filename;
            } else {
                if (outputFile != null) {
                    throw new IllegalArgumentException(
                            "Multiple output redirections are not supported."
                    );
                }

                outputFile = filename;
                appendOutput = operator.equals(">>");
            }
        }

        return new ParsedCommand(
                command,
                List.copyOf(arguments),
                Map.of(),
                Set.of(),
                inputFile,
                outputFile,
                appendOutput
        );
    }


    public List<String> splitPipeline(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        List<String> segments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        char quote = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (quote != 0) {
                current.append(c);

                if (c == quote) {
                    quote = 0;
                }
            } else if (c == '"' || c == '\'') {
                quote = c;
                current.append(c);
            } else if (c == '|') {
                String segment = current.toString().trim();

                if (segment.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Missing command around pipe operator."
                    );
                }

                segments.add(segment);
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (quote != 0) {
            throw new IllegalArgumentException("Unclosed quotation mark.");
        }

        String segment = current.toString().trim();

        if (segment.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing command after pipe operator."
            );
        }

        segments.add(segment);
        return List.copyOf(segments);
    }

}
