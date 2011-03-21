package com.sun.tools.javac;

import java.io.PrintWriter;

public class Main {
    public static int compile(String[] args, PrintWriter writer) {
        return Helper.compile(args, writer);
    }
}
