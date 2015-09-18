package com.shawckz.ipractice.listener;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by 360 on 9/18/2015.
 */
public class StaffModeListener implements Listener {

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.isStaffMode()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.isCancelled()) return;
        if(Practice.getCache().getIPlayer(e.getPlayer()).isStaffMode()){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks while in staff mode.");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(e.isCancelled()) return;
        if(Practice.getCache().getIPlayer(e.getPlayer()).isStaffMode()){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED+"You cannot place blocks while in staff mode.");
        }
    }

    @EventHandler
    public void onProjectLaunch(ProjectileLaunchEvent e){
        if(e.isCancelled()) return;
        if(e.getEntity().getShooter() != null && e.getEntity().getShooter() instanceof Player){
            Player shooter = (Player) e.getEntity().getShooter();
            IPlayer ip = Practice.getCache().getIPlayer(shooter);
            if(ip.isStaffMode()){
                e.setCancelled(true);
                shooter.sendMessage(ChatColor.RED+"You cannot do this while in staff mode.");
            }
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            IPlayer ip = Practice.getCache().getIPlayer(p);
            if(ip.isStaffMode()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            Player p = (Player) e.getDamager();
            IPlayer ip = Practice.getCache().getIPlayer(p);
            if(ip.isStaffMode()){
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "You cannot attack while in staff mode.");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            //Staff Mode items
            if(e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() != Material.AIR){
                Material m = e.getPlayer().getItemInHand().getType();
                if(m == Material.EYE_OF_ENDER){
                    e.setCancelled(true);
                    IPlayer ip = Practice.getCache().getIPlayer(e.getPlayer());
                    if(ip.isStaffMode()){
                        ip.setStaffTeleportShuffle(!ip.isStaffTeleportShuffle());
                        ip.getPlayer().sendMessage(ChatColor.YELLOW+"Teleport Shuffle: "+
                                (ip.isStaffTeleportShuffle() ? ChatColor.GREEN+"enabled" : ChatColor.RED+"disabled"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e){
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.isStaffMode()){
            if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR){
                if(p.getItemInHand().getType() == Material.BOOK){
                    if(e.getRightClicked() instanceof Player){
                        Player target = (Player) e.getRightClicked();
                        openInspectInventory(p, target);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            IPlayer ip = Practice.getCache().getIPlayer(p);
            if(ip.isStaffMode()){
                e.setFoodLevel(20);
            }
        }
    }

    private void openInspectInventory(Player player, Player target){
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLUE+"Inspect: "+target.getName());

        for(int i = 0; i < target.getInventory().getContents().length; i++){
            inv.setItem(i, target.getInventory().getContents()[i]);
        }

        inv.setItem(45, target.getInventory().getHelmet());
        inv.setItem(46, target.getInventory().getChestplate());
        inv.setItem(47, target.getInventory().getLeggings());
        inv.setItem(48, target.getInventory().getBoots());

        inv.setItem(52, new ItemBuilder(Material.COOKED_BEEF).name(ChatColor.GOLD + "Hunger: "+
                ChatColor.AQUA+Math.round(target.getFoodLevel())).build());

        inv.setItem(53, new ItemBuilder(new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData())).name(ChatColor.GOLD +
                "Health: "+ChatColor.AQUA+Math.round(target.getHealth())).build());

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(ChatColor.stripColor(e.getInventory().getName()).startsWith("Inspect: ")){
            if(e.getCurrentItem() != null){
                if(e.getCurrentItem().getType() != Material.AIR){
                    e.setCancelled(true);
                    e.setResult(Event.Result.DENY);
                }
            }
        }
    }

}
