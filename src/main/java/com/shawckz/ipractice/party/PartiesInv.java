package com.shawckz.ipractice.party;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by 360 on 5/13/2015.
 */

/**
 * The PartiesInv class
 * Simple listener that allows a player to view a list of parties from the party
 * inventory, and duel them.
 */
public class PartiesInv implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player){
            if(!e.getInventory().getName().equalsIgnoreCase("Parties to duel")) return;
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            final Player p = (Player) e.getWhoClicked();
            final IPlayer ip = Practice.getCache().getIPlayer(p);
            if(ip.getState() == PlayerState.AT_SPAWN){
                if(ip.getParty() != null){
                    if(ip.getParty().getLeader().equals(p.getName())){

                        if(e.getCurrentItem() != null){
                            if(e.getCurrentItem().getType() != Material.AIR){
                                if(e.getCurrentItem().getType() == Material.SKULL_ITEM){

                                    if(e.getCurrentItem().hasItemMeta()){
                                        final Party party = fromDisplayName(e.getCurrentItem().getItemMeta().getDisplayName());

                                        if(party!=null){

                                            if(party.canDuel()){
                                                if(!party.hasDuel(ip.getParty())){
                                                    //player.getParty, party
                                                    p.closeInventory();
                                                    e.setCancelled(true);
                                                    e.setResult(Event.Result.DENY);
                                                    p.performCommand("pduel "+party.getLeader());
                                                }
                                            }
                                            else{
                                                p.sendMessage(ChatColor.RED+"That party is not at spawn.");
                                            }

                                        }
                                        else{
                                            p.sendMessage(ChatColor.RED+"Party not found.");
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void open(Player p){
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.getParty() == null) return;
        Inventory inv = Bukkit.createInventory(null,54,"Parties to duel");

        for(Party party : Practice.getPartyManager().getParties()){
            if(!ip.getParty().getLeader().equals(party.getLeader())){
                if(party.canDuel()){
                    inv.addItem(new ItemBuilder(Material.SKULL_ITEM).name(ChatColor.AQUA + party.getLeader() + "'s Party").lore(ChatColor.GOLD+party.getMembersToString()).build());
                }
            }
        }
        p.openInventory(inv);
    }

    private static Party fromDisplayName(String s){
        s = ChatColor.stripColor(s);
        s = s.split("'")[0];
        s = s.replaceAll("Party","");
        s = s.replaceAll(" ","");
        Party party = Practice.getPartyManager().getParty(Bukkit.getPlayer(s));
        return party;
    }

}
