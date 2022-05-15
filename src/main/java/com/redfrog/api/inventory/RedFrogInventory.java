package com.redfrog.api.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import com.redfrog.api.RedFrogPlugin;
import com.redfrog.api.inventory.presets.BasicInventory;
import com.redfrog.api.inventory.presets.CustomInventory;

import java.util.Arrays;

public class RedFrogInventory implements InventoryHolder {

    private RedFrogInventory previousInventory;

    private final boolean unregisterOnClose;
    private InventoryCloseEvent inventoryCloseEvent;
    private CustomInventory page;

    Inventory inventory;
    private final int maxX;
    private final int maxY;

    private InventoryListener listener;

    public RedFrogInventory(RedFrogPlugin plugin, String title, int width, int height, boolean unregisterOnClose) {
        this.maxX = width;
        this.maxY = height;

        inventory = Bukkit.createInventory(this, width * height, ChatColor.translateAlternateColorCodes('&', title));
        page = new BasicInventory(0, 0);
        this.unregisterOnClose = unregisterOnClose;

        register(plugin);
    }


    public RedFrogInventory getPreviousInventory() {
        return previousInventory;
    }


    public String getTitle() {
        return inventory.getTitle();
    }


    public CustomInventory getPreset() {
        return page;
    }


    public RedFrogInventory setPage(CustomInventory page) {
        this.page.detach(this);

        page.onReplace(this.page);

        this.page = page;
        return this;
    }


    public RedFrogInventory setClonedPage(CustomInventory page) {
        CustomInventory p = page.clonePreset();

        p.refresh();

        setPage(p);
        return this;
    }


    public RedFrogInventory update() {
        ItemStack[] items = page.getItems();

        if (items.length > maxX * maxY)
            items = Arrays.copyOf(items, maxX * maxY);

        inventory.setContents(items);
        return this;
    }


    public RedFrogInventory setOnInventoryCloseListener(InventoryCloseEvent listener) {
        this.inventoryCloseEvent = listener;

        return this;
    }


    void onInventoryClick(InventoryClickEvent event) {

        event.setCancelled(true);

        if (!inventory.equals(event.getClickedInventory()))
            return;

        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {

            int x = event.getSlot() % maxX;
            int y = event.getSlot() / maxX;

            page.onSlotClick(new InventorySlotClickData(this, x, y, event.getSlot(), event));
        }
    }


    void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {

        page.detach(this);

        if (inventoryCloseEvent != null)
            inventoryCloseEvent.onInventoryClose(event);

        if (unregisterOnClose)
            listener.unregisterInventory(this);
    }


    public RedFrogInventory show(Player p) {

        if (p.getOpenInventory() != null) {

            Inventory inventory = p.getOpenInventory().getTopInventory();

            if (listener.containsInventory(inventory)) {
                previousInventory = listener.getInventory(inventory);
            }
        }

        replace(p);
        return this;
    }


    public RedFrogInventory replace(Player p) {
        p.openInventory(inventory);
        listener.registerInventory(this);

        return this;
    }


    public RedFrogInventory applyPage(CustomInventory page) {
        setPage(page);
        return apply();
    }


    public RedFrogInventory applyClonedPage(CustomInventory page) {
        setClonedPage(page);
        return apply();
    }


    public RedFrogInventory replacePreset(CustomInventory preset) {
        this.page.detach(this);
        this.page = preset;

        return apply();
    }


    protected RedFrogInventory register(RedFrogPlugin plugin) {
        return register(plugin.getInventoryListener());
    }


    protected RedFrogInventory register(InventoryListener listener)
    {
        this.listener = listener;
        listener.registerInventory(this);
        return this;
    }


    protected RedFrogInventory unregister(RedFrogPlugin plugin) {
        plugin.unregisterInventory(this);
        return this;
    }


    public RedFrogInventory apply() {

        page.attach(this);
        update();

        return this;
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }


    public interface InventoryCloseEvent {
        public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent e);
    }
}
