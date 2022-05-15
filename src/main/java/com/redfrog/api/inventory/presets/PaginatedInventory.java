package com.redfrog.api.inventory.presets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.redfrog.api.pattern.buffering.PatternBuffer;
import com.redfrog.api.inventory.InventorySlotClickData;

import java.util.List;

public class PaginatedInventory extends BackableInventory {

    private ItemStack backgroundItem;
    private PatternBuffer backgroundBuffer;
    private PatternBuffer area;
    private List<ItemStack> items;
    private ItemStack emptySlotItem;

    int prevPageSlot;
    int nextPageSlot;
    int curPage;


    @Override
    protected void init() {
        super.init();

        curPage = 0;
        prevPageSlot = CustomInventory.getSlot(0, 5, 9);
        nextPageSlot = CustomInventory.getSlot(8, 5, 9);
    }


    public PaginatedInventory setContentArea(PatternBuffer buffer) {
        this.area = buffer;
        return this;
    }


    public PaginatedInventory setBackgroundArea(PatternBuffer buffer, ItemStack item) {
        this.backgroundItem = item;
        this.backgroundBuffer = buffer;
        return this;
    }


    public PaginatedInventory setContent(List<ItemStack> items) {
        this.items = items;

        return this;
    }


    public PaginatedInventory setEmptySlotItem(ItemStack material) {
        this.emptySlotItem = material;
        return this;
    }


    @Override
    public boolean onSlotClick(InventorySlotClickData data) {

        if (data.getSlot() == prevPageSlot) {
            previousPage();
            return true;
        } else if (data.getSlot() == nextPageSlot) {
            nextPage();
            return true;
        } else {
            return super.onSlotClick(data);
        }
    }


    private void showPreviousBtn(boolean visible) {
        if (visible)
            setItem(prevPageSlot, Material.ARROW, "&ePrevious");
        else
            removeItem(prevPageSlot);
    }


    private void showNextBtn(boolean visible) {
        if (visible)
            setItem(nextPageSlot, Material.ARROW, "&eNext");
        else
            removeItem(nextPageSlot);
    }


    public void nextPage() {
        curPage++;
        refresh();
    }


    public void previousPage() {
        curPage--;
        refresh();
    }


    @Override
    public BasicInventory refresh() {
        updatePage();

        return super.refresh();
    }


    private void drawBackground() {
        if (backgroundBuffer == null)
            return;
        ;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (backgroundBuffer.getAbsoluteObject(x, y) != null)
                    setItem(x, y, backgroundItem);
            }
        }
    }


    private void updatePage() {

        clear();

        drawBackground();

        if (area != null) {
            int itemsPerPage = area.getCount();

            float totalPages = (items.size() / (float) itemsPerPage);

            if ((int) totalPages < totalPages)
                totalPages = (int) totalPages + 1;
            else
                totalPages = (int) totalPages;

            if (totalPages < 1)
                totalPages = 1;

            if (curPage >= totalPages)
                curPage = (int) totalPages - 1;

            if (curPage < 0)
                curPage = 0;

            if (totalPages == 1) {
                showPreviousBtn(false);
                showNextBtn(false);
            } else if (totalPages > 1) {
                if (curPage < totalPages - 1)
                    showNextBtn(true);
                else
                    showNextBtn(false);

                if (curPage > 0)
                    showPreviousBtn(true);
                else
                    showPreviousBtn(false);
            }

            int endItem = (curPage + 1) * itemsPerPage;
            int endItemWithEmptySlots = endItem;
            ItemStack curItem;

            if (endItem >= items.size())
                endItem = items.size();

            if (emptySlotItem != null)
                endItem = endItemWithEmptySlots;

            int i = curPage * itemsPerPage;

            for (int y = 0; y < area.getHeight() && i < endItem; y++) {
                for (int x = 0; x < area.getWidth() && i < endItem; x++) {

                    if (area.getRelativeObject(x, y) != null) {

                        if (i < items.size())
                            curItem = items.get(i);
                        else
                            curItem = emptySlotItem;

                        setItem(area.getStartX() + x, area.getStartY() + y, curItem);
                        i++;
                    }
                }
            }
        }
    }


    public void setPage(int page) {
        curPage = page;
        updatePage();
    }
}
