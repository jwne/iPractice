package com.shawckz.ipractice.match;

import com.shawckz.ipractice.util.ItemBuilder;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by 360 on 5/18/2015.
 */
public class MatchInventory {

    @Getter
    private static Map<String,MatchInventory> matchInventories = new HashMap<>();

    @Getter
    private final Player player;
    @Getter
    private Inventory inv;
    @Getter
    private String uuid;

    public MatchInventory(final Player player) {
        this.player = player;
        this.inv = Bukkit.createInventory(null, 54, "Viewing Inventory");
        uuid = UUID.randomUUID().toString().toLowerCase();

        for(int i = 0; i < player.getInventory().getContents().length; i++){
            inv.setItem(i,player.getInventory().getContents()[i]);
        }


        inv.setItem(45, player.getInventory().getHelmet());
        inv.setItem(46, player.getInventory().getChestplate());
        inv.setItem(47, player.getInventory().getLeggings());
        inv.setItem(48, player.getInventory().getBoots());

        inv.setItem(52, new ItemBuilder(Material.COOKED_BEEF).name(ChatColor.GOLD + "Hunger: "+ChatColor.AQUA+Math.round(player.getFoodLevel())).build());
        inv.setItem(53, new ItemBuilder(new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData())).name(ChatColor.GOLD +
                "Health: "+ChatColor.AQUA+Math.round(player.getHealth())).build());

        matchInventories.put(uuid,this);
    }

    public void open(Player p){
        p.openInventory(inv);
    }

    public static MatchInventory getMatchInventory(String uuid){
        return matchInventories.get(uuid);
    }


}
