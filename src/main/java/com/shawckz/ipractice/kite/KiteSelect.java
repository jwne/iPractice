package com.shawckz.ipractice.kite;


import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class KiteSelect implements Listener{

    private final Player player;
    private static final String inventoryName = ChatColor.BLUE+"Kite Practice";

    private static final Inventory inv;
    static {
        inv = Bukkit.createInventory(null, 9, inventoryName);
        inv.addItem(new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.GOLD+"Challenge a player to chase you").build());
        inv.addItem(new ItemBuilder(Material.DIAMOND).name(ChatColor.GOLD+"Challenge a player to run from you").build());
        inv.addItem(new ItemBuilder(Material.DIAMOND_SWORD).name(ChatColor.GOLD+"Join Kite Practice "+ChatColor.LIGHT_PURPLE+"Chaser "+ChatColor.GOLD+"Queue").build());
        inv.addItem(new ItemBuilder(Material.ENDER_PEARL).name(ChatColor.GOLD+"Join Kite Practice "+ChatColor.LIGHT_PURPLE+"Runner "+ChatColor.GOLD+"Queue").build());
    }

    public KiteSelect(Player player){
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){

    }

}
