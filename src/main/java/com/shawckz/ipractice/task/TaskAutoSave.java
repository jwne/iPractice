package com.shawckz.ipractice.task;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TaskAutoSave implements Runnable {

    @Override
    public void run() {
        int saved = 0;
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(Practice.getCache().contains(pl.getName())){
                IPlayer iPlayer = Practice.getCache().getIPlayer(pl);
                iPlayer.update();
                saved++;
            }
        }
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"[AutoSave] Successfully saved "+saved+" players to the database.");
    }
}
