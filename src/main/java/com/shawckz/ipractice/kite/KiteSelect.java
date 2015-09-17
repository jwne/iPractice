package com.shawckz.ipractice.kite;


import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.util.ItemBuilder;
import com.shawckz.ipractice.util.chatlisten.ChatListenCallback;

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

public class KiteSelect implements Listener{

    private final Player player;
    private static final String mainInventoryName = ChatColor.BLUE+"Kite Practice";
    private static final String duelInventoryName = ChatColor.BLUE+"Kite Duel";
    private static final String queueInventoryName = ChatColor.BLUE+"Kite Queue";

    private static final Inventory mainInventory;
    private static final Inventory queueInventory;
    private static final Inventory duelInventory;
    static {
        {
            mainInventory = Bukkit.createInventory(null, 9, mainInventoryName);
            mainInventory.setItem(2, new ItemBuilder(Material.BLAZE_ROD)
                    .name(duelInventoryName)
                    .lore(ChatColor.GRAY+"Challenge other players to kite duels.")
                    .build());
            mainInventory.setItem(6, new ItemBuilder(Material.NETHER_STAR)
                    .name(queueInventoryName)
                    .lore(ChatColor.GRAY + "Get matched with other players in kite duels.")
                    .build());
        }
        {
            duelInventory = Bukkit.createInventory(null, 9, mainInventoryName);
            duelInventory.setItem(2, new ItemBuilder(Material.GOLD_INGOT)
                    .name(ChatColor.GOLD + "Challenge a player to chase you").build());
            duelInventory.setItem(6, new ItemBuilder(Material.DIAMOND)
                    .name(ChatColor.GOLD + "Challenge a player to run from you").build());
        }
        {
            queueInventory = Bukkit.createInventory(null, 9, queueInventoryName);
            queueInventory.setItem(2, new ItemBuilder(Material.GOLD_INGOT)
                    .name(ChatColor.GOLD + "Join the Kite " + ChatColor.LIGHT_PURPLE + "Runner " + ChatColor.GOLD + "Queue").build());
            queueInventory.setItem(6, new ItemBuilder(Material.DIAMOND)
                    .name(ChatColor.GOLD + "Join the Kite " + ChatColor.LIGHT_PURPLE + "Chaser " + ChatColor.GOLD + "Queue").build());
        }
    }

    public KiteSelect(Player player){
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());

        player.openInventory(mainInventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(((Player)e.getWhoClicked()).getName().equals(player.getName())){
            if(e.getCurrentItem() != null){
                if(e.getCurrentItem().getType() != Material.AIR){
                    ItemStack i = e.getCurrentItem();
                    Material m = i.getType();

                    if(e.getInventory().getName().equalsIgnoreCase(mainInventoryName)){
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                        if(m == Material.BLAZE_ROD){
                            player.openInventory(duelInventory);
                            Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
                        }
                        else if (m == Material.NETHER_STAR){
                            player.openInventory(queueInventory);
                            Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
                        }
                    }
                    else if (e.getInventory().getName().equalsIgnoreCase(queueInventoryName)){
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                        player.closeInventory();
                        if(m == Material.GOLD_INGOT){
                            player.sendMessage(ChatColor.GOLD+"Kite Queues coming soon!");
                        }
                        else if (m == Material.DIAMOND){
                            player.sendMessage(ChatColor.GOLD+"Kite Queues coming soon!");
                        }
                    }
                    else if (e.getInventory().getName().equalsIgnoreCase(duelInventoryName)){
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                        player.closeInventory();
                        if(m == Material.GOLD_INGOT){
                            player.sendMessage(ChatColor.GOLD+"Enter the name of the player you wish to duel.");
                            new ChatListenCallback(player){
                                @Override
                                public void callback(String message) {
                                    Player target = Bukkit.getPlayer(message);
                                    if(target != null){
                                        unregister();
                                        new KiteRequest(Practice.getCache().getIPlayer(player), Practice.getCache().getIPlayer(target), player, target)
                                                .send();
                                    }
                                    else{
                                        player.sendMessage(ChatColor.RED+"Player '"+message+"' not found.");
                                    }
                                }
                            };
                        }
                        else if (m == Material.DIAMOND){
                            player.sendMessage(ChatColor.GOLD+"Enter the name of the player you wish to duel.");
                            new ChatListenCallback(player){
                                @Override
                                public void callback(String message) {
                                    Player target = Bukkit.getPlayer(message);
                                    if(target != null){
                                        unregister();
                                        new KiteRequest(Practice.getCache().getIPlayer(player), Practice.getCache().getIPlayer(target), target, player)
                                                .send();
                                    }
                                    else{
                                        player.sendMessage(ChatColor.RED+"Player '"+message+"' not found.");
                                    }
                                }
                            };
                        }
                    }

                    HandlerList.unregisterAll(this);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer() instanceof Player){
            Player p = (Player) e.getPlayer();
            if(this.player.getName().equals(p.getName())){
                if(e.getInventory().getName().equalsIgnoreCase(duelInventoryName)
                        || e.getInventory().getName().equalsIgnoreCase(queueInventoryName)){
                    player.sendMessage("Unregistered");
                    HandlerList.unregisterAll(this);
                }
            }
        }
    }

}
