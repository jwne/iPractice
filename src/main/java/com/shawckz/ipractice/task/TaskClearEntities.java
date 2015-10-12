package com.shawckz.ipractice.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

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
