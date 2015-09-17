package com.shawckz.ipractice.kite;

import com.shawckz.ipractice.Practice;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class KiteMatchHandler implements Listener {

    private final KiteMatch match;

    public final void register(){
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    public final void unregister(){
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent e){
        if(match.contains(e.getPlayer())){
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!match.contains(pl)){
                    Practice.getEntityHider().hideEntity(pl, e.getItemDrop());
                }
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(e.getItemDrop() != null){
                        e.getItemDrop().remove();
                    }
                }
            }.runTaskLater(Practice.getPlugin(), 60L);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.setDeathMessage(null);
        final Player p = e.getEntity();
        if(match.contains(p)){
            List<ItemStack> newDrops = new ArrayList<>();
            newDrops.addAll(e.getDrops());
            e.getDrops().clear();
            final Set<Item> items = new HashSet<>();
            for(ItemStack i : newDrops){
                Item item = p.getWorld().dropItemNaturally(p.getLocation(), i);
                items.add(item);
            }

            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!match.contains(pl)){
                    for(Item item : items){
                        Practice.getEntityHider().hideEntity(pl, item);
                    }
                }
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    for(Item item : items){
                        if(item != null){
                            item.remove();
                        }
                    }
                }
            }.runTaskLater(Practice.getPlugin(), 60L);

            match.eliminatePlayer(p);

            AutoRespawn.autoRespawn(e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(match.contains(p)){
            match.eliminatePlayer(p);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        final Player p = e.getPlayer();
        if(e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()){
            //They only moved their head, ignore.
            return;
        }
        if(match.contains(p)){
            if(match.getRole(p) == KiteRole.RUNNER){
                if(match.getRunnerCountdown() > 0){
                    e.setTo(e.getFrom());
                }
            }
            else{
                if(match.getChaserCountdown() > 0){
                    e.setTo(e.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();
            if(match.contains(p)){
                if(match.contains(d)){
                    if (match.getRole(d) == KiteRole.RUNNER){
                        e.setCancelled(true);
                        d.sendMessage(ChatColor.RED+"You cannot attack as the runner!");
                    }
                    else if(match.getRole(d) == KiteRole.CHASER){
                        if(match.getChaserCountdown() > 0){
                            e.setCancelled(true);
                        }
                    }
                }
                else{
                    //They aren't in the same match, cancel the damage
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(match.contains(p)){
            if(!match.isStarted()){
                if(p.getItemInHand().getType() == Material.ENDER_PEARL){
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.RED + "You cannot do this until the match has started.");
                }
            }
            else{
                if(p.getItemInHand().getType() == Material.ENDER_PEARL){
                    if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
                        IPlayer ip = Practice.getCache().getIPlayer(p);
                        if(ip.getEnderpearl() > System.currentTimeMillis()){
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.RED+"Enderpearl Cooldown: "+ChatColor.GOLD+((ip.getEnderpearl() - System.currentTimeMillis())/1000));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPearl(ProjectileLaunchEvent e){
        if(!e.isCancelled()){
            if(e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof EnderPearl){
                Player p = (Player) e.getEntity().getShooter();
                IPlayer ip = Practice.getCache().getIPlayer(p);
                ip.setEnderpearl(System.currentTimeMillis() + (1000 * 15));
                ip.getScoreboard().update();
            }
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent e){
        if(e.getPotion().getShooter() instanceof Player){
            Player p = (Player) e.getPotion().getShooter();
            if(match.contains(p)){
                //IPlayer ip = Practice.getCache().getIPlayer(p);
                Iterator<LivingEntity> li = e.getAffectedEntities().iterator();

                while(li.hasNext()){
                    LivingEntity en = li.next();
                    if(en instanceof Player){
                        Player pl = (Player) en;
                        if(!match.contains(pl)){
                            e.setIntensity(en,0);
                            e.getAffectedEntities().remove(en);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e){
        Projectile proj = e.getEntity();
        if(proj.getShooter() != null){
            if(proj.getShooter() instanceof Player){
                Player p = (Player) proj.getShooter();

                if(match.contains(p)){
                    for(Player pl : Bukkit.getOnlinePlayers()){
                        if(!match.contains(pl)){
                            Practice.getEntityHider().hideEntity(pl,proj);
                        }
                    }
                }
            }
        }
    }

}
