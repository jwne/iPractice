package com.shawckz.ipractice.kit;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.KitException;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.serial.KitSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class KitHandler {

    private final IPlayer player;
    private final YamlConfiguration config;
    private final File configFile;
    private final KitSerializer serializer = new KitSerializer();

    public KitHandler(IPlayer player) {
        this.player = player;

        try {
            File players = new File(Practice.getPlugin().getDataFolder().getPath() + File.separator + "players");
            if (!players.exists()) {
                players.mkdir();
            }
            File uu = new File(Practice.getPlugin().getDataFolder().getPath() + File.separator + "players" +
                    File.separator + player.getUniqueId());
            if (!uu.exists()) {
                uu.mkdir();
            }
            File f = new File(Practice.getPlugin().getDataFolder().getPath() + File.separator +
                    "players" + File.separator + player.getUniqueId() + File.separator + "kit.yml");
            if (!f.exists()) {
                f.createNewFile();
            }
            this.configFile = f;
            config = YamlConfiguration.loadConfiguration(f);
        } catch (IOException ex) {
            throw new KitException("Could not save kit for " + player.getName(), ex);
        }
    }

    public void save(Kit kit) {
        config.set("kits."+kit.getName(), serializer.toString(kit));
        saveConfig();
    }

    public Kit load(String name) {
        return serializer.fromString(config.getString("kits."+name));
    }

    public boolean hasKitSaved(String name){
        return config.contains("kits."+name);
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            throw new KitException("Could not save kit config to file", ex);
        }
    }

}
