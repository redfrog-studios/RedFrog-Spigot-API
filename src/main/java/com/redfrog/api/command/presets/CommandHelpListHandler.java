package com.redfrog.api.command.presets;

import com.redfrog.api.RedFrogPlugin;
import com.redfrog.api.command.CommandExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandHelpListHandler extends ListCommand {


    protected List<CommandExecutor> commands = new ArrayList<>();


    public void setCommands(List<CommandExecutor> commands)
    {
        this.commands = commands;
    }


    public CommandHelpListHandler(RedFrogPlugin plugin) {
        super(plugin);
    }


    public CommandHelpListHandler(RedFrogPlugin plugin, String description, String... args)
    {
        super(plugin, description);
        this.setArgs(args);
        this.setCanHaveMoreParams(true);
    }


    public CommandHelpListHandler(RedFrogPlugin plugin, String name, String description, String... args) {
        super(plugin, name, description, args);
        this.setCanHaveMoreParams(true);
    }


    @Override
    protected String getSinglePageHeader(String cmd) {
        return "&e---------- &f" + plugin.getDescription().getName() + ": Help &e----------------";
    }


    @Override
    protected String getMultiPageHeader(String cmd, int page, int totalPages) {
        return "&e---------- &f" + plugin.getDescription().getName() + ": Help (" + page + "/" + (int) totalPages + ") &e----------\n" +
                "&7Use /" + cmd + " help <n> to see the page n.";
    }


    @Override
    protected int getItemsCount() {
        return commands.size();
    }


    @Override
    protected String getItem(String cmd, int index) {

        String name = cmd.toLowerCase();

        if (!commands.get(index).getUsage().equals(""))
            name += " " + commands.get(index).getUsage();

        return "&6/" +  name + ": &f" + commands.get(index).getDescription();
    }
}
