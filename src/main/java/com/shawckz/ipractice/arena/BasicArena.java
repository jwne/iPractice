package com.shawckz.ipractice.arena;


import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.configuration.annotations.ConfigData;
import com.shawckz.ipractice.configuration.annotations.ConfigSerializer;
import com.shawckz.ipractice.serial.LocationSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Getter
@Setter
public class BasicArena extends Arena {

    @ConfigData("id")
    private final int id;

    @ConfigData("name")
    private String name;

    @ConfigData("spawns.alpha")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location spawnAlpha;

    @ConfigData("spawns.bravo")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location spawnBravo;

    @ConfigData("points.min")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location min;

    @ConfigData("points.max")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location max;

    public BasicArena(Plugin plugin, int id, String name, Location spawnAlpha, Location spawnBravo, Location min, Location max) {
        super(plugin, "arenas" + File.separator + id +".yml");
        this.id = id;
        this.name = name;
        this.spawnAlpha = spawnAlpha;
        this.spawnBravo = spawnBravo;
        this.min = min;
        this.max = max;
        load();
        save();
    }

    public BasicArena(Plugin plugin, int id) {
        super(plugin, "arenas" + File.separator + id + ".yml");
        this.id = id;
        load();
    }

    @Override
    public ArenaType getType() {
        return ArenaType.NORMAL;
    }

    @Override
    public BasicArena duplicate(int offsetX, int offsetZ) {
        BasicArena arena = new BasicArena(Practice.getPlugin(), Practice.getArenaManager().getNextArenaIndex(), name, spawnAlpha, spawnBravo, min, max);
        arena.setSpawnAlpha(spawnAlpha.clone().add(offsetX, 0, offsetZ));
        arena.setSpawnBravo(spawnBravo.clone().add(offsetX, 0, offsetZ));
        arena.setMin(min.clone().add(offsetX, 0, offsetZ));
        arena.setMax(max.clone().add(offsetX, 0, offsetZ));
        arena.setHasMatch(false);
        return arena;
    }
}
