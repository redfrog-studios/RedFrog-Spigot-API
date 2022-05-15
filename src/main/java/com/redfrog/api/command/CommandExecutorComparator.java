package com.redfrog.api.command;

import java.util.Comparator;

public class CommandExecutorComparator implements Comparator<CommandExecutor> {


    private static CommandExecutorComparator instance;


    public static CommandExecutorComparator getInstance() {
        return instance;
    }


    static {
        instance = new CommandExecutorComparator();
    }


    @Override
    public int compare(CommandExecutor o1, CommandExecutor o2) {

        String s1 = String.join(" ", o1.getArgs()).toLowerCase();
        String s2 = String.join(" ", o2.getArgs()).toLowerCase();

        return s1.compareTo(s2);
    }

}
