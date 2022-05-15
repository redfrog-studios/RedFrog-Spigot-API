package com.redfrog.api.inventory.presets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.redfrog.api.pattern.buffering.PatternBuffer;
import com.redfrog.api.inventory.RedFrogInventory;
import com.redfrog.api.inventory.InventorySlotClickData;

public interface CustomInventory extends Cloneable {

    public interface InventorySlotClickEvent {
        public void onSlotClick(InventorySlotClickData data);
    }

    public int getWidth();
    public int getHeight();

    public boolean onSlotClick(InventorySlotClickData data);

    public CustomInventory setOnSlotClickListener(InventorySlotClickEvent listener);

    public ItemStack[] getItems();

    public CustomInventory clear();

    public CustomInventory removeItem(int x, int y);


    public CustomInventory setItem(int x, int y, Material material);

    public CustomInventory setItem(int x, int y, Material material, String name);

    public CustomInventory setItem(int x, int y, Material material, String name, String lore);

    public CustomInventory setItem(int x, int y, Material material, String name, String lore, int amount);

    public CustomInventory setItem(int x, int y, Material material, String name, String lore, int amount, boolean isEnchanted);

    public CustomInventory setItem(int x, int y, ItemStack item);


    public CustomInventory removeItem(int slot);


    public CustomInventory setItem(int slot, Material material);

    public CustomInventory setItem(int slot, Material material, String name);

    public CustomInventory setItem(int slot, Material material, String name, String lore);

    public CustomInventory setItem(int slot, Material material, String name, String lore, int amount);

    public CustomInventory setItem(int slot, Material material, String name, String lore, int amount, boolean isEnchanted);

    public CustomInventory setItem(int slot, ItemStack item);


    public CustomInventory fillX(int y, Material material);

    public CustomInventory fillY(int x, Material material);

    public CustomInventory fill(ItemStack itemStack);
    public CustomInventory fill(Material material);
    public CustomInventory fill(Material material, String name);
    public CustomInventory fill(Material material, String name, String lore);
    public CustomInventory fill(Material material, String name, String lore, int amount);
    public CustomInventory fill(Material material, String name, String lore, int amount, boolean isEnchanted);

    public CustomInventory fill(PatternBuffer buffer, Material material);

    public CustomInventory apply(RedFrogInventory inventory);

    public CustomInventory attach(RedFrogInventory inventory);

    public CustomInventory detach(RedFrogInventory inventory);

    // Do not call this. This is used internally to indicate when a preset is being replaced.
    public CustomInventory onReplace(CustomInventory preset);

    public CustomInventory update();

    public CustomInventory refresh();

    public CustomInventory clonePreset();

    public static int getSlot(int x, int y, int width) {
        return width * y + x;
    }
}
