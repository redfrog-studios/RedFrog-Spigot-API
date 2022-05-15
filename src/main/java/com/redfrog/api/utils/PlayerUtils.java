package com.redfrog.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    /**
     * Get the name displayed by a player.
     *
     * @param player Player to get the name.
     * @return
     */
    public static String getDisplayName(UUID player) {
        return getDisplayName(Bukkit.getOfflinePlayer(player));
    }

    /**
     * Get the name displayed by a player.
     *
     * @param player Player to get the name.
     * @return
     */
    public static String getDisplayName(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();

        if (onlinePlayer != null)
            return onlinePlayer.getDisplayName();
        else
            return player.getName();
    }
}
