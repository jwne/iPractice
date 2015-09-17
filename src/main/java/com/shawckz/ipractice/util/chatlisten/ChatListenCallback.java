package com.shawckz.ipractice.util.chatlisten;

import com.shawckz.ipractice.Practice;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class ChatListenCallback implements Listener {

    private final Player player;

    public ChatListenCallback(Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
        player.sendMessage(ChatColor.GRAY+"Your chat has been disabled.  Type \"exit\" to cancel.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(e.getPlayer().getName().equals(player.getName())){
            e.setCancelled(true);
            if(e.getMessage().equalsIgnoreCase("exit")){
                unregister();
            }
            else{
                callback(e.getMessage());
            }
        }
        else{
            e.getRecipients().remove(player);
        }
    }

    public abstract void callback(String message);

    public void unregister(){
        HandlerList.unregisterAll(this);
        player.sendMessage(ChatColor.GRAY+"Your chat is now enabled.");
    }

}
