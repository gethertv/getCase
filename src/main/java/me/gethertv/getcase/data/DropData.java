package me.gethertv.getcase.data;

import org.bukkit.inventory.ItemStack;

public class DropData {

    private ItemStack itemStack;
    private double chance;
    private int slot;


    public DropData(ItemStack itemStack, double chance, int slot) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getChance() {
        return chance;
    }

    public int getSlot() {
        return slot;
    }
}
