package com.shawckz.ipractice.event.events;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.event.EventType;
import com.shawckz.ipractice.event.PracticeEvent;
import com.shawckz.ipractice.exception.PracticeEventException;
import com.shawckz.ipractice.player.IPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class EventLMS extends PracticeEvent {

    private boolean started = false;

    private int countdown = 15;

    private final Set<IPlayer> players = new HashSet<>();

    public EventLMS(Practice practice) {
        super(practice, EventType.LMS);
    }

    @Override
    public void startEvent() {
        if(!started){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(countdown > 0){
                        msg("");
                        countdown--;
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


}
