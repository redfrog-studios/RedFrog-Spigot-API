package com.redfrog.api.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.redfrog.api.RedFrogPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {


    /**
     * Creates an item from a material name.
     *
     * @param materialName Material of the item.
     * @return
     */
    public static ItemStack fromMaterialName(String materialName) {
        if (materialName.endsWith("_STAINED_GLASS_PANE"))
            return GlassColor.valueOf(materialName.replace("_STAINED_GLASS_PANE", "")).toItem(true);
        else if (materialName.endsWith("_STAINED_GLASS"))
            return GlassColor.valueOf(materialName.replace("_STAINED_GLASS", "")).toItem(true);

        return new ItemStack(Material.valueOf(materialName));
    }


    /**
     * Creates an item from a material name.
     *
     * @param materialName Material of the item.
     * @param itemName     Name to display in the item.
     * @return
     */
    public static ItemStack fromMaterialName(String materialName, String itemName) {
        return fromMaterialName(materialName, itemName, new ArrayList<>());
    }


    /**
     * Creates an item from a material name.
     *
     * @param materialName Material of the item.
     * @param itemName     Name to display in the item.
     * @param itemLore     Lore of the item.
     * @return
     */
    public static ItemStack fromMaterialName(String materialName, String itemName, String itemLore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(itemLore);

        return fromMaterialName(materialName, itemName, loreList);
    }


    /**
     * Creates an item from a material name.
     *
     * @param materialName Material of the item.
     * @param itemName     Name to display in the item.
     * @param itemLore     Lore of the item.
     * @return
     */
    public static ItemStack fromMaterialName(String materialName, String itemName, List<String> itemLore) {
        ItemStack item = fromMaterialName(materialName);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.format(itemName));

        if (itemLore != null && itemLore.size() > 0) {
            List<String> coloredLore = itemLore.stream().map(CC::format).collect(Collectors.toList());

            meta.setLore(coloredLore);
        }

        item.setItemMeta(meta);

        return item;
    }


    /**
     * Creates an item from a material name on the plugin main config.
     *
     * @param plugin           Plugin to request the config.
     * @param materialNamePath Path to the material name in the config.
     * @return
     */
    public static ItemStack fromMainConfig(RedFrogPlugin plugin, String materialNamePath) {
        String materialName = plugin.getConfig().getString(materialNamePath);
        return fromMaterialName(materialName);
    }


    /**
     * Creates an item from a material name on the plugin main config.
     *
     * @param plugin           Plugin to request the config.
     * @param materialNamePath Path to the material name in the config.
     * @param itemName         Name to display in the item.
     * @return
     */
    public static ItemStack fromMainConfig(RedFrogPlugin plugin, String materialNamePath, String itemName) {
        String materialName = plugin.getConfig().getString(materialNamePath);
        return fromMaterialName(materialName, itemName);
    }


    /**
     * Creates an item from a material name on the plugin main config.
     *
     * @param plugin           Plugin to request the config.
     * @param materialNamePath Path to the material name in the config.
     * @param itemName         Name to display in the item.
     * @param itemLore         Lore of the item.
     * @return
     */
    public static ItemStack fromMainConfig(RedFrogPlugin plugin, String materialNamePath, String itemName, String itemLore) {
        String materialName = plugin.getConfig().getString(materialNamePath);
        return fromMaterialName(materialName, itemName, itemLore);
    }


    /**
     * Creates an item from a material name on the plugin main config.
     *
     * @param plugin           Plugin to request the config.
     * @param materialNamePath Path to the material name in the config.
     * @param itemName         Name to display in the item.
     * @param itemLore         Lore of the item.
     * @return
     */
    public static ItemStack fromMainConfig(RedFrogPlugin plugin, String materialNamePath, String itemName, List<String> itemLore) {
        String materialName = plugin.getConfig().getString(materialNamePath);
        return fromMaterialName(materialName, itemName, itemLore);
    }
}
