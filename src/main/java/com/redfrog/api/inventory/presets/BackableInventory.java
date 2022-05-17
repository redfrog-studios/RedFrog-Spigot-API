package com.redfrog.api.inventory.presets;

import com.redfrog.api.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.redfrog.api.inventory.InventorySlotClickData;

public class BackableInventory extends BasicInventory {


    protected int backItemSlot = 0;
    private ItemStack backItem;

    private CustomInventory previousPreset;

    private boolean showBackButton;


    public void enableBackButton(boolean enable) {
        showBackButton = enable;
    }


    @Override
    protected void init() {
        super.init();

        showBackButton = false;

        backItemSlot = CustomInventory.getSlot(4, 5, width);
        backItem = new ItemStack(Material.BOOK);
        ItemMeta meta = backItem.getItemMeta();

        meta.setDisplayName(CC.format("&eBack"));
        backItem.setItemMeta(meta);
    }


    @Override
    public BasicInventory refresh() {

        if (showBackButton)
            setItem(backItemSlot, backItem);

        return super.refresh();
    }


    /**
     * Set the item that will be used to go back.
     * @param x X pos in the inventory.
     * @param y Y pos in the innventory.
     * @param item Item to display in the inventory.
     */
    public void setBackItem(int x, int y, ItemStack item) {
        backItem = item;
        backItemSlot = CustomInventory.getSlot(x, y, width);

        refresh();
    }


    @Override
    public boolean onSlotClick(InventorySlotClickData data) {
        if (!showBackButton)
            return super.onSlotClick(data);

        if (data.getSlot() == backItemSlot) {
            if (previousPreset != null) {

                if (previousPreset.getWidth() == 0 && previousPreset.getHeight() == 0) {
                    if (data.getInventory().getPreviousInventory() != null) {
                            data.getInventory().getPreviousInventory().replace((Player) data.getEvent().getWhoClicked());
                    }
                } else {
                    data.getInventory().replacePreset(previousPreset);
                }
            }

            return true;
        } else {
            return super.onSlotClick(data);
        }
    }


    @Override
    public CustomInventory onReplace(CustomInventory preset) {
        super.onReplace(preset);

        previousPreset = preset;

        return this;
    }
}
