package com.shawckz.ipractice.party;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class PartyEventSelect implements Listener{

    private final Player player;
    private final String invName = ChatColor.BLUE+"Party Events";

    public PartyEventSelect(Player player) {
        this.player = player;

        Inventory inv = Bukkit.createInventory(null, 9, invName);

        for(PartyEvent event : PartyEvent.values()){
            inv.addItem(new ItemBuilder(event.getIcon()).name(ChatColor.GOLD+event.getName()).build());
        }

        player.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(((Player)e.getWhoClicked()).getName().equals(player.getName())){
            if(e.getInventory().getName().equalsIgnoreCase(invName)){
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType() != Material.AIR){
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                        player.closeInventory();
                        ItemStack i = e.getCurrentItem();
                        if(i.hasItemMeta() && i.getItemMeta().getDisplayName() != null){
                            String name = ChatColor.stripColor(i.getItemMeta().getDisplayName().replaceAll(" ","_"));
                            if(PartyEvent.fromString(name) != null){
                                onSelect(PartyEvent.fromString(name));
                                HandlerList.unregisterAll(this);
                                return;
                            }
                            else{
                                player.sendMessage(ChatColor.RED+"Unknown Party Event.");
                                HandlerList.unregisterAll(this);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer() instanceof Player){
            Player p = (Player) e.getPlayer();
            if(this.player.getName().equals(p.getName())){
                if(e.getInventory().getName().equalsIgnoreCase(invName)){
                    HandlerList.unregisterAll(this);
                }
            }
        }
    }

    public abstract void onSelect(PartyEvent event);

}
