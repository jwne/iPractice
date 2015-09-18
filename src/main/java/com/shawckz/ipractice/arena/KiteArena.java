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
public class KiteArena extends Arena {

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

    @ConfigData("end")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location end;

    public KiteArena(Plugin plugin, String name, Location spawnAlpha, Location spawnBravo, Location end) {
        super(plugin, "arenas" + File.separator + name+".yml");
        this.name = name;
        this.spawnAlpha = spawnAlpha;
        this.spawnBravo = spawnBravo;
        this.end = end;
        this.id = (Practice.getArenaManager().getArenaIndex()+1);
        Practice.getArenaManager().setArenaIndex((id+1));
        load();
        save();
    }

    public KiteArena(Plugin plugin, String name) {
        super(plugin, "arenas" + File.separator + name + ".yml");
        this.name = name;
        load();
    }

    @Override
    public ArenaType getType() {
        return ArenaType.KITE;
    }
}
