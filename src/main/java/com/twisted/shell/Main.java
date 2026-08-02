package com.twisted.shell;

import com.twisted.shell.shell.Shell;

public class Main {

    public static void main(String[] args) {

        System.out.println("""
                ======================================
                      TwistedShell
                Git Inspired CLI Framework
                ======================================
                """);

        Shell shell = new Shell();

        shell.start();

    }

}