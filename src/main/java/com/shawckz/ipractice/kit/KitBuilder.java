package com.shawckz.ipractice.kit;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by 360 on 09/05/2015.
 */

/**
 * The KitBuilder class
 * Used when sending a player to the kit building area, so that they can
 * make their kit, save it and return to spawn.
 */
@RequiredArgsConstructor
public class KitBuilder implements Listener {

    @Getter @NonNull Ladder ladder;
    @Getter @NonNull private Player player;
    @Getter private boolean active = false;

    /**
     * Initialize the kitBuilder, teleports the player, heals them, and makes all other players invisible to them.
     */
    public void init(){
        Practice.getCache().getIPlayer(player).setState(PlayerState.BUILDING_KIT);
        Practice.getCache().getIPlayer(player).setKitBuilder(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, Practice.getPlugin());
        player.teleport(Practice.getIConfig().getKitBuilderSpawn());
        player.setHealth(20);
        player.setFoodLevel(20);
        Practice.getCache().getIPlayer(player).equipKit(ladder);
        for(Player pl : Bukkit.getOnlinePlayers()){
            player.hidePlayer(pl);
        }
        active = true;
        Practice.getCache().getIPlayer(player).getScoreboard().update();
    }

    /**
     * Call to unregister the builder and show other players
     */
    public void exit(){
        HandlerList.unregisterAll(this);
        for(Player pl : Bukkit.getOnlinePlayers()){
            player.showPlayer(pl);
        }
        active = false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        IPlayer iPlayer = Practice.getCache().getIPlayer(p);
        if(p.getName().equals(player.getName())){
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
                if(e.getClickedBlock().getType() == Material.CHEST){
                    e.setCancelled(true);
                    Inventory inv = Bukkit.createInventory(null,54,"Kit Creation");
                    inv.setContents(ladder.getInventory().getContents());
                    p.openInventory(inv);
                }
                else if (e.getClickedBlock().getType() == Material.SIGN
                        || e.getClickedBlock().getType() == Material.SIGN_POST
                        || e.getClickedBlock().getType() == Material.WALL_SIGN){
                    Sign sign = (Sign) e.getClickedBlock().getState();
                    if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_GREEN+"- Save Kit -")){
                        e.setCancelled(true);
                        iPlayer.setKit(ladder, Kit.fromInventory(p, ladder.getName()));
                        iPlayer.getKitHandler().save(iPlayer.getKit(ladder));
                        p.sendMessage(ChatColor.GREEN+"Kit saved.");
                    }
                    else if (sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_RED+"- Spawn -")){
                        e.setCancelled(true);
                        p.setItemInHand(new ItemStack(Material.AIR));
                        exit();
                        iPlayer.sendToSpawn();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSign(SignChangeEvent e){
        if(e.getPlayer().isOp()){
            if(e.getLine(0).equalsIgnoreCase("Save Kit")){
                e.setLine(0,ChatColor.DARK_GREEN+"- Save Kit -");
            }
            else if (e.getLine(0).equalsIgnoreCase("Spawn")){
                e.setLine(0,ChatColor.DARK_RED+"- Spawn -");
            }
        }
    }

}
