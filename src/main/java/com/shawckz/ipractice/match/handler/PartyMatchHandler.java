package com.shawckz.ipractice.match.handler;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.MatchHandler;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.util.AutoRespawn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PartyMatchHandler extends MatchHandler {

    private final Match match;

    private Map<String,String> lastDamages = new HashMap<>();

    public PartyMatchHandler(Plugin plugin, Match match) {
        super(plugin, match);
        this.match = match;
    }

    @EventHandler
    public void onItemDrop(final ItemSpawnEvent e){
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(!match.hasPlayer(pl)){
                Practice.getEntityHider().hideEntity(pl, e.getEntity());
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(e.getEntity() != null){
                            e.getEntity().remove();
                        }
                    }
                }.runTaskLater(Practice.getPlugin(), 60L);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.setDeathMessage(null);
        final Player p = e.getEntity();
        final IPlayer ip = Practice.getCache().getIPlayer(p);
        if(match.hasPlayer(p)){

            if(match.isRanked()){

                ip.getDeaths().put(match.getLadder(), ip.getDeaths().get(match.getLadder())+1);

                if(p.getKiller() != null){
                    IPlayer kip = Practice.getCache().getIPlayer(p.getKiller());
                    kip.getKills().put(match.getLadder(), kip.getKills().get(match.getLadder())+1);
                }
                else if (lastDamages.containsKey(p.getName())){
                    Player t = Bukkit.getPlayerExact(lastDamages.get(p.getName()));
                    if(t != null){
                        IPlayer kip = Practice.getCache().getIPlayer(t);
                        kip.getKills().put(match.getLadder(), kip.getKills().get(match.getLadder())+1);
                    }
                }
            }

            match.eliminate(p);

            AutoRespawn.autoRespawn(e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if(match.hasPlayer(e.getPlayer())){
            Player p = e.getPlayer();
            IPlayer ip = Practice.getCache().getIPlayer(p);

            if(match.isRanked()){

                ip.getDeaths().put(match.getLadder(), ip.getDeaths().get(match.getLadder())+1);

                if(p.getKiller() != null){
                    IPlayer kip = Practice.getCache().getIPlayer(p.getKiller());
                    kip.getKills().put(match.getLadder(), kip.getKills().get(match.getLadder())+1);
                }
                else if (lastDamages.containsKey(p.getName())){
                    Player t = Bukkit.getPlayerExact(lastDamages.get(p.getName()));
                    if(t != null){
                        IPlayer kip = Practice.getCache().getIPlayer(t);
                        kip.getKills().put(match.getLadder(), kip.getKills().get(match.getLadder())+1);
                    }
                }
            }


            match.eliminate(e.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(match.hasPlayer(p)){
            if(!match.isStarted()){
                if(p.getItemInHand().getType() == Material.ENDER_PEARL){
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.RED+"You cannot do this until the match has started.");
                }
            }
            else{
                if(p.getItemInHand().getType() == Material.ENDER_PEARL){
                    if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
                        IPlayer ip = Practice.getCache().getIPlayer(p);
                        if(ip.getEnderpearl() > System.currentTimeMillis()){
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.RED+"Pearl Cooldown: "+ChatColor.GOLD+((ip.getEnderpearl() - System.currentTimeMillis())/1000));
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
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();

            if(match.hasPlayer(d)){
                if(match.hasPlayer(p)){
                    if(!match.isStarted()){
                        e.setCancelled(true);
                        e.setDamage(0.0);
                        d.sendMessage(ChatColor.RED+"You cannot attack until the match has started!");
                    }
                    else if (match.getTeam(p) == match.getTeam(d)){
                        e.setCancelled(true);
                        e.setDamage(0.0);
                        d.sendMessage(ChatColor.RED+p.getName()+" is on your team!");
                    }
                }
                else{
                    //Attempted to damage someone who is not in their match
                    e.setCancelled(true);
                    e.setDamage(0.0);
                }
            }
            else if (!match.hasPlayer(d) && match.hasPlayer(p)){
                e.setCancelled(true);
                e.setDamage(0.0);
            }
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent e){
        if(e.getPotion().getShooter() instanceof Player){
            Player p = (Player) e.getPotion().getShooter();
            if(match.hasPlayer(p)){
                //IPlayer ip = Practice.getCache().getIPlayer(p);
                Iterator<LivingEntity> li = e.getAffectedEntities().iterator();

                while(li.hasNext()){
                    LivingEntity en = li.next();
                    if(en instanceof Player){
                        Player pl = (Player) en;
                        if(!match.hasPlayer(pl)){
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

                if(match.hasPlayer(p)){
                    for(Player pl : Bukkit.getOnlinePlayers()){
                        if(!match.hasPlayer(pl)){
                            Practice.getEntityHider().hideEntity(pl,proj);
                        }
                    }
                }
            }
        }
    }

}
