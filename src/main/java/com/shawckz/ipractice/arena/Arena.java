package com.shawckz.ipractice.arena;

import com.shawckz.ipractice.configuration.Configuration;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public abstract class Arena extends Configuration {

    public Arena(Plugin plugin, String filename) {
        super(plugin, filename);
    }

    private boolean hasMatch = false;

    public abstract String getName();

    public abstract Location getSpawnAlpha();

    public abstract Location getSpawnBravo();

    public abstract int getId();

    public abstract ArenaType getType();

    public abstract void setSpawnAlpha(Location loc);

    public abstract void setSpawnBravo(Location loc);

    public boolean isHasMatch() {
        return hasMatch;
    }

    public void setHasMatch(boolean hasMatch) {
        this.hasMatch = hasMatch;
    }

    public abstract Location getMax();

    public abstract Location getMin();

    public abstract Arena duplicate(int offsetX, int offsetZ);

    public abstract void setMin(Location min);

    public abstract void setMax(Location max);

}
