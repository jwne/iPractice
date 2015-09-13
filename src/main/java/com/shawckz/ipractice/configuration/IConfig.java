package com.shawckz.ipractice.configuration;

import com.shawckz.ipractice.configuration.annotations.ConfigData;
import com.shawckz.ipractice.configuration.annotations.ConfigSerializer;
import com.shawckz.ipractice.serial.LocationSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
public class IConfig extends Configuration {

    public IConfig(Plugin plugin) {
        super(plugin);
        load();
        save();
    }

    @ConfigData("spawn")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location spawn = Bukkit.getWorld("world").getSpawnLocation();

    @ConfigData("kitbuilder.spawn")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location kitBuilderSpawn = Bukkit.getWorld("world").getSpawnLocation();

    @ConfigData("scoreboard.title")
    private String scoreboardTitle = "&6&lPractice";

}
