package com.shawckz.ipractice.listener;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.util.nametag.NametagManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onJoinHide(PlayerJoinEvent e){
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        ip.handlePlayerVisibility();
        NametagManager.setup(p);
    }

    @EventHandler
    public void onJoinNoMsg(PlayerJoinEvent e){
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onQuitNoMsg(PlayerQuitEvent e){
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.getParty() != null){
            p.performCommand("party leave");
        }
        NametagManager.remove(p);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            IPlayer ip = Practice.getCache().getIPlayer(p);
            if(ip.getState() != PlayerState.IN_MATCH){
                e.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(p.getGameMode() == GameMode.CREATIVE && (ip.getState() == PlayerState.IN_MATCH || ip.getState() == PlayerState.AT_SPAWN)
                && p.isOp()){
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e){
        new BukkitRunnable(){
            @Override
            public void run() {
                IPlayer ip = Practice.getCache().getIPlayer(e.getPlayer());
                ip.sendToSpawn();
            }
        }.runTaskLater(Practice.getPlugin(), 5L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            IPlayer ip = Practice.getCache().getIPlayer(p);
            if(ip.getState() != PlayerState.IN_MATCH){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent e){
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.getState() == PlayerState.AT_SPAWN){
            e.setCancelled(true);
        }
        else if(ip.getState() == PlayerState.BUILDING_KIT){
            e.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getInventory().getName().equalsIgnoreCase("Viewing Inventory")){
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

}
