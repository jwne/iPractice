package com.shawckz.ipractice.spawn.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class SpawnItem {

    public abstract ItemStack getItem();

    public abstract int getSlot();

    public abstract SpawnItemType getType();

    public void give(Player p){
        p.getInventory().setItem(getSlot(), getItem());
    }

    public abstract SpawnItemAction getAction();

}
