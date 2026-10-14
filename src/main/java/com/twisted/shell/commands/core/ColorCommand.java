package com.twisted.shell.commands.core;

import com.twisted.shell.commandStructure.Command;
import com.twisted.shell.commandStructure.CommandCategory;
import com.twisted.shell.commandStructure.CommandContext;

import java.util.List;
import java.util.Map;

import static com.twisted.shell.library.color.*;
import static com.twisted.shell.library.color.BG_GREEN;
import static com.twisted.shell.library.color.BG_WHITE;

public class ColorCommand implements Command {

    @Override
    public String name() {return "color"; }

    @Override
    public String description() {return "Change color of text and Background";}

    @Override
    public CommandCategory category() {return CommandCategory.CORE;}

    @Override
    public String usage() {return "color";}

    @Override
    public List<String> aliases() {return List.of();}

    @Override
    public void execute(CommandContext context) {
        Map<Character, String> foregroundColors = Map.ofEntries(
                Map.entry('0', BLACK),
                Map.entry('1', RED),
                Map.entry('2', GREEN),
                Map.entry('3', YELLOW),
                Map.entry('4', BLUE),
                Map.entry('5', PURPLE),
                Map.entry('6', CYAN),
                Map.entry('7', WHITE),
                Map.entry('8', BRIGHT_BLACK),
                Map.entry('9', BRIGHT_RED),
                Map.entry('A', BRIGHT_GREEN),
                Map.entry('B', BRIGHT_YELLOW),
                Map.entry('C', BRIGHT_BLUE),
                Map.entry('D', BRIGHT_PURPLE),
                Map.entry('E', BRIGHT_CYAN),
                Map.entry('F', BRIGHT_WHITE)
        );
        Map<Character, String> backgroundColors = Map.ofEntries(
                Map.entry('0', BG_BLACK),
                Map.entry('1', BG_RED),
                Map.entry('2', BG_GREEN),
                Map.entry('3', BG_YELLOW),
                Map.entry('4', BG_BLUE),
                Map.entry('5', BG_PURPLE),
                Map.entry('6', BG_CYAN),
                Map.entry('7', BG_WHITE),
                Map.entry('8', BG_BRIGHT_BLACK),
                Map.entry('9', BRIGHT_RED),
                Map.entry('A', BG_BRIGHT_GREEN),
                Map.entry('B', BG_BRIGHT_YELLOW),
                Map.entry('C', BG_BRIGHT_BLUE),
                Map.entry('D', BG_BRIGHT_PURPLE),
                Map.entry('E', BG_BRIGHT_CYAN),
                Map.entry('F', BG_BRIGHT_WHITE)
        );

        List<String> arguments = context.arguments();
        if (arguments.isEmpty()) {
            context.shell().terminal().println("Usage: color <code|RESET>");
            return;
        }
        String code = arguments.getFirst().toUpperCase();
        if(code.equals("RESET")){
            context.shell().terminal().println(RESET);
        }
        if (code.length() != 2) {
            context.shell().terminal().println("Invalid color code. Use two hexadecimal characters (0-F).");
            return;
        }
        if (!foregroundColors.containsKey(code.charAt(1))
                || !backgroundColors.containsKey(code.charAt(0))) {
            context.shell().terminal().println("Invalid color code. Use characters 0-F.");
            return;
        }
        System.out.print(foregroundColors.get(code.charAt(0)) + backgroundColors.get(code.charAt(1)));
    }
}