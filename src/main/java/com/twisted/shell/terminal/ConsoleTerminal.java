package com.twisted.shell.terminal;

import java.util.Scanner;

public class ConsoleTerminal implements Terminal {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String readLine() {

        System.out.print("twisted> ");

        return scanner.nextLine();

    }

    @Override
    public void print(String text) {

        System.out.print(text);

    }

    @Override
    public void println(String text) {

        System.out.println(text);

    }

}