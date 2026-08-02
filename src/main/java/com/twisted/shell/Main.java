package com.twisted.shell;

import com.twisted.shell.shell.Shell;
import com.twisted.shell.core.BuildInfo;

public class Main {

    public static void main(String[] args) {

        System.out.printf("""
                ======================================
                      TwistedShell
                Git Inspired CLI Framework
                ======================================
                """,
                BuildInfo.name(),
                BuildInfo.version());

        Shell shell = new Shell();

        shell.start();

    }

}