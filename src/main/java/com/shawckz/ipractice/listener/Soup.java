package com.shawckz.ipractice.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by 360 on 5/15/2015.
 */

/**
 * The Soup class / Listener
 * Used to create the soup mechanic, where upon
 * a player right clicking a soup, it is instantly eaten and
 * gives them 3.5 (7hp) hearts.
 */
public class Soup implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void FastSoup(PlayerInteractEvent e){
        Player p = e.getPlayer();
        int hp = 7;
        double h = p.getHealth();
        Action a = e.getAction();
        if(h != p.getMaxHealth()){
            if(h != 0){
                if ((a == Action.RIGHT_CLICK_AIR) || (a == Action.RIGHT_CLICK_BLOCK)){
                    if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP){
                        e.setCancelled(true);
                        p.setHealth(h + hp > (p).getMaxHealth() ?  (p).getMaxHealth() : h + hp);
                        p.getItemInHand().setType(Material.BOWL);
                        p.updateInventory();
                    }
                }
            }
        }
    }

}
