package com.twisted.shell.history;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private final List<String> history = new ArrayList<>();

    public void add(String command) {
        history.add(command);
    }

    public List<String> getAll() {
        return List.copyOf(history);
    }
}