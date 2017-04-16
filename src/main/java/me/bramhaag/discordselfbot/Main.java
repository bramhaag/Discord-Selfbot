package me.bramhaag.discordselfbot;

import com.google.common.base.Preconditions;

public class Main {

    public static Bot bot;

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 1, "Please specify a token");

        bot = new Bot(args[0]);
    }
}
