package com.redfrog.api.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InventoryListener implements Listener {

    private HashMap<Inventory, RedFrogInventory> inventories = new HashMap<>();


    public void registerInventory(RedFrogInventory inventory) {
        inventories.put(inventory.inventory, inventory);
    }


    public void unregisterInventory(RedFrogInventory inventory) {
        inventories.remove(inventory.inventory);
    }


    public RedFrogInventory getInventory(Inventory inventory)
    {
        return inventories.get(inventory);
    }


    public boolean containsInventory(Inventory inventory)
    {
        return inventories.containsKey(inventory);
    }


    @EventHandler
    private void onInventorySlotClick(InventoryClickEvent event) {
        if (inventories.containsKey(event.getInventory()))
            inventories.get(event.getInventory()).onInventoryClick(event);
    }


    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (inventories.containsKey(event.getInventory()))
            inventories.get(event.getInventory()).onInventoryClose(event);
    }
}
