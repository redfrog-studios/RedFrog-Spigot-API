package com.redfrog.api.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import com.redfrog.api.RedFrogPlugin;

public class Message {


    private static Boolean usePlaceHolderAPI = false;


    public static void init(RedFrogPlugin plugin) {
        usePlaceHolderAPI = plugin.isUsingPlaceholderAPI();
    }


    /**
     * Format a message with colors and PlaceholderAPI.
     * @param message Message to be formatted.
     * @return
     */
    public static String fromString(String message, boolean usePlaceHolderAPI) {
        return fromString(null, message, usePlaceHolderAPI);
    }


    /**
     * Format a message with colors and PlaceholderAPI.
     * @param player Player to associate with PlaceholderAPI.
     * @param message Message to be formatted.
     * @return
     */
    public static String fromString(Player player, String message, boolean usePlaceHolderAPI) {
        if (usePlaceHolderAPI)
            return CC.format(PlaceholderAPI.setPlaceholders(player, message));
        else
            return CC.format(message);
    }


    /**
     * Get a formatted message from the main config with colors and PlaceholderAPI.
     * @param plugin Plugin to request the config.
     * @param path Path to get the message in the YAML config.
     * @return
     */
    public static String fromMainConfig(RedFrogPlugin plugin, String path, boolean usePlaceHolderAPI) {
        return fromString(plugin.getConfig().getString(path), usePlaceHolderAPI);
    }


    /**
     * Get a formatted message from the main config with colors and PlaceholderAPI.
     * @param plugin Plugin to request the config.
     * @param path Path to get the message in the YAML config.
     * @param player Player to associate with PlaceholderAPI.
     * @return
     */
    public static String fromMainConfig(RedFrogPlugin plugin, String path, Player player, boolean usePlaceHolderAPI) {
        return fromString(player, plugin.getConfig().getString(path), usePlaceHolderAPI);
    }


    public static String fromString(String message) {
        return fromString(null, message, usePlaceHolderAPI);
    }

    public static String fromString(Player player, String message) {
        return fromString(player, message, usePlaceHolderAPI);
    }

    public static String fromMainConfig(RedFrogPlugin plugin, String path) {
        return fromMainConfig(plugin, path, usePlaceHolderAPI);
    }

    public static String fromMainConfig(RedFrogPlugin plugin, String path, Player player) {
        return fromMainConfig(plugin, path, player, usePlaceHolderAPI);
    }
}
