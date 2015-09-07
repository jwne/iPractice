package com.shawckz.ipractice.match;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class MatchHandler implements Listener{

    private final Plugin plugin;
    private final Match match;

    public MatchHandler(Plugin plugin, Match match) {
        this.plugin = plugin;
        this.match = match;
    }

    public void register(){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister(){
        HandlerList.unregisterAll(this);
    }

}
