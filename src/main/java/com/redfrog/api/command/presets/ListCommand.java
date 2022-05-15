package com.redfrog.api.command.presets;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.redfrog.api.RedFrogPlugin;

public abstract class ListCommand extends CustomCommand {


    public ListCommand(RedFrogPlugin plugin) {
        super(plugin);
    }


    public ListCommand(RedFrogPlugin plugin, String description, String... args) {
        super(plugin, "", description, true, args);
    }


    public ListCommand(RedFrogPlugin plugin, String name, String description, String... args) {
        super(plugin, name, description, true, args);
    }

    protected int getItemsPerPage() {
        return 5;
    }

    protected String getSinglePageHeader(String cmd) {
        return "&e---------- &f" + cmd + ": Info &e----------------";
    }

    protected String getMultiPageHeader(String cmd, int page, int totalPages) {
        return "&e---------- &f" + cmd + ": Info (" + page + "/" + totalPages + ") &e----------\n" +
                "&7Use /" + cmd + " help <n> to see the page n.";
    }

    protected abstract int getItemsCount();

    protected abstract String getItem(String cmd, int index);

    @Override
    public void onCommand(CommandSender sender, String cmd, String[] params) {
        int page = 0;

        float totalPages = (getItemsCount() / (float) getItemsPerPage());

        if ((int) totalPages < totalPages)
            totalPages = (int) totalPages + 1;
        else
            totalPages = (int) totalPages;

        if (totalPages < 1)
            totalPages = 1;

        if (params.length > 0) {
            try {
                page = Integer.parseInt(params[0]);
                page--;
                if (page >= totalPages)
                    page = 0;

                if (page < 0)
                    page = 0;
            } catch (Exception e) {
                page = 0;
            }
        }

        if (totalPages == 1)
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getSinglePageHeader(cmd)));
        else
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMultiPageHeader(cmd, page + 1, (int) totalPages)));

        int endItem = (page + 1) * getItemsPerPage();

        for (int i = page * getItemsPerPage(); i < endItem && i < getItemsCount(); i++) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getItem(cmd, i)));
        }
    }
}
