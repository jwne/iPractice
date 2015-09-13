package com.shawckz.ipractice.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class Kit {

    private String name;
    private ItemStack[] armor;
    private ItemStack[] inventory;

    public static Kit fromInventory(Player p, String name){
        return new Kit(name, p.getInventory().getArmorContents(), p.getInventory().getContents());
    }

    public static Kit emptyKit(){
        return new Kit("None", new ItemStack[4], new ItemStack[36]);
    }

}
