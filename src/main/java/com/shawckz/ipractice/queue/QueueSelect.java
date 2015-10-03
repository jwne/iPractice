package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.type.PartyQueue;
import com.shawckz.ipractice.util.ItemBuilder;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
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

public abstract class QueueSelect implements Listener {

    @Getter
    private IPlayer p;

    @Getter
    private Inventory inv;

    public QueueSelect(IPlayer p) {
        this.p = p;
        Bukkit.getServer().getPluginManager().registerEvents(this, Practice.getPlugin());

        inv = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Select a Queue");

        for (Queue queue : Practice.getQueueManager().getQueues().values()) {
            if(queue.canJoin(p)){
                inv.addItem(new ItemBuilder(queue.getIcon())
                        .name(ChatColor.GOLD + queue.getType().getName())
                        .lore(ChatColor.BLUE + "" + ChatColor.ITALIC + queue.getType().getDescription())
                        .lore(ChatColor.GRAY + "Players in " + WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                + " queue: " + ChatColor.AQUA + queue.getMembers().size())
                        .build());
            }
        }

        p.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (((Player) e.getWhoClicked()).getName().equals(p.getName())) {
            if (e.getInventory().getName().equalsIgnoreCase(ChatColor.BLUE + "Select a Queue")) {
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().getType() != Material.AIR) {
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                        p.getPlayer().closeInventory();
                        ItemStack i = e.getCurrentItem();
                        if (i.hasItemMeta() && i.getItemMeta().getDisplayName() != null) {
                            String name = ChatColor.stripColor(i.getItemMeta().getDisplayName()).replaceAll(" ","_");
                            if (QueueType.fromString(name.toUpperCase()) != null) {
                                onSelect(QueueType.fromString(name.toUpperCase()));
                                HandlerList.unregisterAll(this);
                                return;
                            } else {
                                p.getPlayer().sendMessage(ChatColor.RED + "Could not find a queue that matches that");
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
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();
            if (this.p.getName().equals(p.getName())) {
                if (e.getInventory().getName().equalsIgnoreCase(ChatColor.BLUE + "Select a Queue")) {
                    HandlerList.unregisterAll(this);
                }
            }
        }
    }

    public abstract void onSelect(QueueType type);

}
