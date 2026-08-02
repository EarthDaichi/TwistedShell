package com.twisted.shell.terminal;

public interface Terminal {

    String readLine();

    void print(String text);

    void println(String text);

}