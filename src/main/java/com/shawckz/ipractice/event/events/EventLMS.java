package com.shawckz.ipractice.event.events;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.event.EventType;
import com.shawckz.ipractice.event.PracticeEvent;
import com.shawckz.ipractice.exception.PracticeEventException;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class EventLMS extends PracticeEvent {

    private boolean started = false;

    private int countdown = 60;

    private final Set<IPlayer> players = new HashSet<>();

    private final Arena arena;

    public EventLMS(Practice practice) {
        super(practice, EventType.LMS);
        arena = Practice.getArenaManager().getNextArena();
    }

    @Override
    public void startEvent() {
        if(!started){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(countdown > 0){
                        if(countdown % 10 == 0 && countdown > 0 || countdown <= 5){
                            broadcast("Starting in "+ChatColor.GREEN+countdown+ChatColor.BLUE+". Type /join!");
                        }
                        countdown--;
                    }
                    else{
                        int x = 0;
                        for(IPlayer pl : players){
                            pl.setState(PlayerState.IN_MATCH);
                            pl.handlePlayerVisibility();
                            if(x % 2 == 0){
                                pl.getPlayer().teleport(arena.getSpawnAlpha());
                            }
                            else{
                                pl.getPlayer().teleport(arena.getSpawnBravo());
                            }
                            x++;
                        }
                        broadcast("The event has started with "+ChatColor.GREEN+players.size()+ChatColor.BLUE+" players.");
                        cancel();
                    }
                }
            }.runTaskTimer(Practice.getPlugin(), 20L ,20L);
        }
        else{
            throw new PracticeEventException("Tried to start event when already started");
        }
    }

    @Override
    public void endEvent() {
        for(IPlayer pl : players){
            pl.sendToSpawn();
        }
        msg("The event has ended.");
    }

    @Override
    public void addPlayer(IPlayer player) {
        if(!hasPlayer(player)){
            players.add(player);
        }
        else{
            throw new PracticeEventException("Attempted to add player when that player is already in the event");
        }
    }

    @Override
    public boolean hasPlayer(IPlayer player) {
        return players.contains(player);
    }

    public void msg(String msg){
        for(IPlayer pl : players){
            pl.getPlayer().sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+"(EVENT) "+ ChatColor.RESET+""+
                    ChatColor.LIGHT_PURPLE+"["+getName()+"] "+ChatColor.BLUE+msg);
        }
    }

    public void broadcast(String msg){
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "(EVENT) " + ChatColor.RESET + "" +
                ChatColor.LIGHT_PURPLE + "[" + getName() + "] " + ChatColor.BLUE + msg);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){

    }

}
