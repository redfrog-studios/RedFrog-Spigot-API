package com.redfrog.api.inventory.presets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.redfrog.api.inventory.InventorySlotClickData;
import com.redfrog.api.inventory.RedFrogInventory;
import com.redfrog.api.pattern.buffering.PatternBuffer;

import java.util.Arrays;
import java.util.HashSet;

public class BasicInventory implements CustomInventory, Cloneable {

    protected HashSet<RedFrogInventory> attachedInventories;

    protected ItemStack[] items;
    protected int width;
    protected int height;

    private InventorySlotClickEvent slotClickListener;

    public BasicInventory() {
        items = new ItemStack[54];
        attachedInventories = new HashSet<>();

        this.width = 9;
        this.height = 6;

        init();
    }


    public BasicInventory(int width, int height) {
        items = new ItemStack[width * height];

        this.width = width;
        this.height = height;

        init();
    }


    protected void init() {
        attachedInventories = new HashSet<>();
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean onSlotClick(InventorySlotClickData data) {

        if (slotClickListener != null)
            slotClickListener.onSlotClick(data);

        return true;
    }


    @Override
    public BasicInventory setOnSlotClickListener(InventorySlotClickEvent listener) {

        this.slotClickListener = listener;
        return this;
    }


    @Override
    public ItemStack[] getItems() {
        return items;
    }


    @Override
    public BasicInventory clear() {

        Arrays.fill(items, null);

        return this;
    }


    @Override
    public BasicInventory removeItem(int x, int y) {
        removeItem(width * y + x);
        return this;
    }


    @Override
    public BasicInventory setItem(int x, int y, Material material) {

        return setItem(width * y + x, material);
    }

    @Override
    public BasicInventory setItem(int x, int y, Material material, String name) {
        return setItem(width * y + x, material, name);
    }

    @Override
    public BasicInventory setItem(int x, int y, Material material, String name, String lore) {
        return setItem(width * y + x, material, name, lore);
    }

    @Override
    public BasicInventory setItem(int x, int y, Material material, String name, String lore, int amount) {
        return setItem(width * y + x, material, name, lore, amount);
    }

    @Override
    public BasicInventory setItem(int x, int y, Material material, String name, String lore, int amount, boolean isEnchanted) {
        return setItem(width * y + x, material, name, lore, amount, isEnchanted);
    }

    @Override
    public BasicInventory setItem(int x, int y, ItemStack item) {

        return setItem(width * y + x, item);
    }


    @Override
    public BasicInventory removeItem(int slot) {
        items[slot] = null;
        return this;
    }


    @Override
    public BasicInventory setItem(int slot, Material material) {
        return setItem(slot, material, "", "", 1, false);
    }

    @Override
    public BasicInventory setItem(int slot, Material material, String name) {
        return setItem(slot, material, name, "", 1, false);
    }

    @Override
    public BasicInventory setItem(int slot, Material material, String name, String lore) {
        return setItem(slot, material, name, lore, 1, false);
    }

    @Override
    public BasicInventory setItem(int slot, Material material, String name, String lore, int amount) {
        return setItem(slot, material, name, lore, amount, false);
    }

    @Override
    public BasicInventory setItem(int slot, Material material, String name, String lore, int amount, boolean isEnchanted) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        if (!lore.equals(""))
            meta.setLore(Arrays.asList(lore.split("\n")));

        item.setAmount(amount);

        if (isEnchanted) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        }

        item.setItemMeta(meta);

        return setItem(slot, item);
    }

    @Override
    public BasicInventory setItem(int slot, ItemStack item) {
        if (slot < items.length)
            items[slot] = item;

        return this;
    }


    @Override
    public BasicInventory fillX(int y, Material material) {
        for (int i = 0; i < width; i++) {
            setItem(i, y, material);
        }

        return this;
    }


    @Override
    public BasicInventory fillY(int x, Material material) {
        for (int i = 0; i < height; i++) {
            setItem(x, i, material);
        }

        return this;
    }


    @Override
    public BasicInventory fill(Material material) {
        return fill(material, "", "", 1, false);
    }

    @Override
    public BasicInventory fill(Material material, String name) {
        return fill(material, name, "", 1, false);
    }

    @Override
    public BasicInventory fill(Material material, String name, String lore) {
        return fill(material, name, lore, 1, false);
    }

    @Override
    public BasicInventory fill(Material material, String name, String lore, int amount) {
        return fill(material, name, lore, amount, false);
    }

    @Override
    public BasicInventory fill(Material material, String name, String lore, int amount, boolean isEnchanted) {

        //Todo: Optimization. Itemstacks are being created everytime, but they are all the same. Create one and fill everything with it.

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        if (!lore.equals(""))
            meta.setLore(Arrays.asList(lore.split("\n")));

        item.setAmount(amount);

        if (isEnchanted) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        }

        item.setItemMeta(meta);

        return fill(item);
    }

    @Override
    public BasicInventory fill(ItemStack item) {

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int slot = width * y + x;
                if (slot < items.length)
                    items[slot] = item;
            }
        }

        return this;
    }


    @Override
    public BasicInventory fill(PatternBuffer buffer, Material material) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (buffer.getAbsoluteObject(x, y) != null)
                    setItem(x, y, material);
            }
        }

        return this;
    }


    @Override
    public BasicInventory apply(RedFrogInventory inventory) {
        inventory.setPage(this);
        inventory.apply();


        return this;
    }


    @Override
    public BasicInventory attach(RedFrogInventory inventory) {
        attachedInventories.add(inventory);
        return this;
    }

    @Override
    public BasicInventory detach(RedFrogInventory inventory) {
        attachedInventories.remove(inventory);
        return this;
    }

    @Override
    public CustomInventory onReplace(CustomInventory preset) {
        return this;
    }


    @Override
    public BasicInventory update() {
        attachedInventories.forEach(RedFrogInventory::update);
        return this;
    }


    @Override
    public BasicInventory refresh() {
        update();
        return this;
    }


    @Override
    public BasicInventory clonePreset() {
        try {
            BasicInventory preset = (BasicInventory) clone();
            preset.items = Arrays.copyOf(items, items.length);
            preset.init();

            return preset;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
