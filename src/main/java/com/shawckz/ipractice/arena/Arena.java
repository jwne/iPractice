package com.shawckz.ipractice.arena;

import com.shawckz.ipractice.configuration.Configuration;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public abstract class Arena extends Configuration {

    public Arena(Plugin plugin, String filename) {
        super(plugin, filename);
    }

    public abstract String getName();

    public abstract Location getSpawnAlpha();

    public abstract Location getSpawnBravo();

    public abstract int getId();

    public abstract ArenaType getType();

    public abstract void setSpawnAlpha(Location loc);

    public abstract void setSpawnBravo(Location loc);

}
