package com.twisted.shell;

import com.twisted.shell.shell.Shell;
import com.twisted.shell.core.BuildInfo;

public class Main {

    public static void main(String[] args) {

        System.out.println("""
                    TwistedShell
                version:""" + BuildInfo.version()
                );
        System.out.println("""
                ____________________________________
                """);

        Shell shell = new Shell();

        shell.start();

    }

}