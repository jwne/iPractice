package com.shawckz.ipractice.listener;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(e.getMessage().startsWith("@") && ip.getParty() != null && e.getMessage().length() >= 2){
            ip.getParty().msg(ChatColor.AQUA+""+ChatColor.BOLD+"(PARTY) "+ChatColor.RESET+""
                    +ChatColor.LIGHT_PURPLE+p.getName()+ChatColor.GRAY+": "+ChatColor.GOLD+e.getMessage().substring(1, e.getMessage().length()));
            e.setCancelled(true);
        }
    }

}
