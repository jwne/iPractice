package com.shawckz.ipractice.match.handle;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.util.AutoRespawn;
import lombok.RequiredArgsConstructor;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by 360 on 9/13/2015.
 */
@RequiredArgsConstructor
public class MatchHandler implements Listener {

    private final Match match;

    private Map<String, String> lastDamages = new HashMap<>();

    @EventHandler
    public void onDrop(final PlayerDropItemEvent e) {
        if (match.getPlayerManager().hasPlayer(e.getPlayer())) {
            ItemStack is = e.getItemDrop().getItemStack();
            ItemMeta im = is.getItemMeta();
            if (im.getLore() == null) {
                im.setLore(new ArrayList<String>());
            }
            im.getLore().add(match.getId());
            is.setItemMeta(im);
            e.getItemDrop().setItemStack(is);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.getItemDrop() != null) {
                        e.getItemDrop().remove();
                    }
                }
            }.runTaskLater(Practice.getPlugin(), 60L);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        Player killer = p.getKiller();
        boolean dropItems = match.getRemainingPlayers() <= 2;
        final IPlayer ip = Practice.getCache().getIPlayer(p);
        if (match.getPlayerManager().hasPlayer(p)) {
            if(dropItems){
                final Set<Item> items = new HashSet<>();
                for (ItemStack i : e.getDrops()) {
                    ItemMeta im = i.getItemMeta();
                    if (im.getLore() == null) {
                        im.setLore(new ArrayList<String>());
                    }
                    im.getLore().add(match.getId());
                    i.setItemMeta(im);
                    Item item = p.getWorld().dropItemNaturally(p.getLocation(), i);
                    items.add(item);
                }
                e.getDrops().clear();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Item item : items) {
                            if (item != null) {
                                item.remove();
                            }
                        }
                        items.clear();
                    }
                }.runTaskLater(Practice.getPlugin(), 60L);
            }


            AutoRespawn.autoRespawn(e);

            if (killer != null) {
                IPlayer kip = Practice.getCache().getIPlayer(killer);
                match.eliminatePlayer(ip, kip);
            } else {
                match.eliminatePlayer(ip, null);
            }
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent e) {
        if (!match.getPlayerManager().hasPlayer(e.getPlayer())) {
            ItemStack i = e.getItem().getItemStack();
            if (i.hasItemMeta()) {
                if (i.getItemMeta().getLore() != null) {
                    if (i.getItemMeta().getLore().contains(match.getId())) {
                        e.setCancelled(true);
                    }
                }
            }
        } else {
            ItemStack i = e.getItem().getItemStack();
            if (i.hasItemMeta()) {
                if (i.getItemMeta().getLore() != null) {
                    if (i.getItemMeta().getLore().contains(match.getId())) {
                        ItemMeta im = i.getItemMeta();
                        im.getLore().remove(match.getId());
                        i.setItemMeta(im);
                        e.getItem().setItemStack(i);
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (match.getPlayerManager().hasPlayer(p)) {
            IPlayer ip = Practice.getCache().getIPlayer(p);
            if (p.getKiller() != null) {
                IPlayer kip = Practice.getCache().getIPlayer(p.getKiller());
                match.eliminatePlayer(ip, kip);
            } else {
                match.eliminatePlayer(ip, null);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (match.getPlayerManager().hasPlayer(p)) {
            if (!match.isStarted()) {
                if (p.getItemInHand().getType() == Material.ENDER_PEARL) {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.RED + "You cannot do this until the match has started.");
                }
            } else {
                if (p.getItemInHand().getType() == Material.ENDER_PEARL) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        IPlayer ip = Practice.getCache().getIPlayer(p);
                        if (ip.getEnderpearl() > System.currentTimeMillis()) {
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.RED + "Still on Ender Pearl cooldown for " + ((ip.getEnderpearl() - System.currentTimeMillis()) / 1000) + " seconds.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPearl(ProjectileLaunchEvent e) {
        if (!e.isCancelled()) {
            if (e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof EnderPearl) {
                Player p = (Player) e.getEntity().getShooter();
                IPlayer ip = Practice.getCache().getIPlayer(p);

                ip.setEnderpearl(System.currentTimeMillis() + (1000 * 15));
                //ip.getScoreboard().update();
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (match.getPlayerManager().hasPlayer(p)) {
                if (!match.isStarted()) {
                    e.setCancelled(true);
                    e.setDamage(0.0);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();

            if (match.getPlayerManager().hasPlayer(d)) {
                if (match.getPlayerManager().hasPlayer(p)) {
                    if (!match.isStarted()) {
                        e.setCancelled(true);
                        e.setDamage(0.0);
                        d.sendMessage(ChatColor.RED + "You cannot attack until the match has started!");
                    } else {
                        if (match.getTeamManager().getTeam(Practice.getCache().getIPlayer(p)).getName()
                                .equalsIgnoreCase(match.getTeamManager().getTeam(Practice.getCache().getIPlayer(d)).getName())) {
                            e.setCancelled(true);
                            e.setDamage(0.0);
                            d.sendMessage(ChatColor.RED + p.getName() + " is on your team!");
                        }
                    }
                } else {
                    //Attempted to damage someone who is not in their match
                    e.setCancelled(true);
                    e.setDamage(0.0);
                }
            } else {
                if (match.getPlayerManager().hasPlayer(p)) {
                    e.setCancelled(true);
                    e.setDamage(0.0);
                }
            }
        } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
            Projectile pd = (Projectile) e.getDamager();
            Player p = (Player) e.getEntity();

            if (pd.getShooter() != null && pd.getShooter() instanceof Player) {
                Player d = (Player) pd.getShooter();
                if (d.getName().equals(p.getName())) return;
                if (match.getPlayerManager().hasPlayer(d)) {
                    if (match.getPlayerManager().hasPlayer(p)) {
                        if (!match.isStarted()) {
                            e.setCancelled(true);
                            e.setDamage(0.0);
                            d.sendMessage(ChatColor.RED + "You cannot attack until the match has started!");
                        } else {
                            if (match.getTeamManager().getTeam(Practice.getCache().getIPlayer(p)).getName()
                                    .equalsIgnoreCase(match.getTeamManager().getTeam(Practice.getCache().getIPlayer(d)).getName())) {
                                e.setCancelled(true);
                                e.setDamage(0.0);
                                d.sendMessage(ChatColor.RED + p.getName() + " is on your team!");
                            }
                        }
                    } else {
                        //Attempted to damage someone who is not in their match
                        e.setCancelled(true);
                        e.setDamage(0.0);
                    }
                } else {
                    if (match.getPlayerManager().hasPlayer(p)) {
                        e.setCancelled(true);
                        e.setDamage(0.0);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent e) {
        if (e.getPotion().getShooter() instanceof Player) {
            Player p = (Player) e.getPotion().getShooter();
            if (match.getPlayerManager().hasPlayer(p)) {
                //IPlayer ip = Practice.getCache().getIPlayer(p);
                Iterator<LivingEntity> li = e.getAffectedEntities().iterator();

                while (li.hasNext()) {
                    LivingEntity en = li.next();
                    if (en instanceof Player) {
                        Player pl = (Player) en;
                        if (!match.getPlayerManager().hasPlayer(pl)) {
                            e.setIntensity(en, 0);
                            e.getAffectedEntities().remove(en);
                        }
                    }
                }
            }
        }
    }

    public final void register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

}
