package com.redfrog.api.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GlassColor {
    WHITE(0),
    ORANGE(1),
    MAGENTA(2),
    LIGHT_BLUE(3),
    YELLOW(4),
    LIME(5),
    PINK(6),
    GRAY(7),
    LIGHT_GRAY(8),
    CYAN(9),
    PURPLE(10),
    BLUE(11),
    BROWN(12),
    GREEN(13),
    RED(14),
    BLACK(15);

    private final short index;

    GlassColor(int index) {
        this.index = ((short) index);
    }

    public int getIndex() {
        return this.index;
    }

    public ItemStack toItem(Boolean isPane) {
        if (isPane)
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, this.index);
        else
            return new ItemStack(Material.STAINED_GLASS, 1, this.index);

    }
}