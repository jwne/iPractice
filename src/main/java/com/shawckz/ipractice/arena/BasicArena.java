package com.shawckz.ipractice.arena;


import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.configuration.annotations.ConfigData;
import com.shawckz.ipractice.configuration.annotations.ConfigSerializer;
import com.shawckz.ipractice.serial.LocationSerializer;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
public class BasicArena extends Arena {

    @ConfigData("id")
    private int id;

    @ConfigData("name")
    private String name;

    @ConfigData("spawns.alpha")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location spawnAlpha;

    @ConfigData("spawns.bravo")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location spawnBravo;

    public BasicArena(Plugin plugin, String name, Location spawnAlpha, Location spawnBravo) {
        super(plugin, "arenas" + File.separator + name+".yml");
        this.name = name;
        this.spawnAlpha = spawnAlpha;
        this.spawnBravo = spawnBravo;
        this.id = (Practice.getArenaManager().getArenaIndex()+1);
        Practice.getArenaManager().setArenaIndex((id+1));
        load();
        save();
    }

    public BasicArena(Plugin plugin, String name) {
        super(plugin, "arenas" + File.separator + name + ".yml");
        this.name = name;
        load();
    }

    @Override
    public ArenaType getType() {
        return ArenaType.NORMAL;
    }
}
