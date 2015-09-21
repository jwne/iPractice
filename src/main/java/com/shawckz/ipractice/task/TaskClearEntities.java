package com.shawckz.ipractice.task;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class TaskClearEntities implements Runnable {

    @Override
    public void run() {
        for(World world : Bukkit.getWorlds()){
            for(Entity e : world.getEntities()){
                if(e.getType() == EntityType.DROPPED_ITEM){
                    e.remove();
                }
            }
        }
    }
}
