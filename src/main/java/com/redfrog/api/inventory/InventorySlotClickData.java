package com.redfrog.api.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public class InventorySlotClickData {

    private RedFrogInventory inventory;
    private int slotX;
    private int slotY;
    private int slot;
    private InventoryClickEvent e;


    public RedFrogInventory getInventory() {return inventory;}

    public int getSlotX()
    {
        return slotX;
    }

    public int getSlotY()
    {
        return slotY;
    }

    public int getSlot()
    {
        return slot;
    }

    public InventoryClickEvent getEvent() {return e;}


    InventorySlotClickData(RedFrogInventory inventory, int slotX, int slotY, int slot, InventoryClickEvent e)
    {
        this.inventory = inventory;
        this.slotX = slotX;
        this.slotY = slotY;
        this.slot = slot;
        this.e = e;
    }
}
