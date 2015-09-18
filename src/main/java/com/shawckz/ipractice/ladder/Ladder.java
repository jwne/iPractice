package com.shawckz.ipractice.ladder;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.configuration.Configuration;
import com.shawckz.ipractice.configuration.annotations.ConfigData;
import com.shawckz.ipractice.configuration.annotations.ConfigSerializer;
import com.shawckz.ipractice.kit.Kit;
import com.shawckz.ipractice.kite.KiteMatch;
import com.shawckz.ipractice.serial.InventorySerializer;
import com.shawckz.ipractice.serial.KitSerializer;
import com.shawckz.ipractice.serial.MaterialSerializer;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Ladder extends Configuration {

    private static final Map<String, Ladder> ladders = new HashMap<>();

    public static Ladder getLadder(String name){
        return ladders.get(name);
    }

    public static void loadLadders(Plugin plugin){
        File dir = new File(plugin.getDataFolder(), "ladders");
        if(!dir.exists()){
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if(files != null) {
            for (File f : files) {
                String name = f.getName();
                name = FilenameUtils.removeExtension(name);
                System.out.println("Loading ladder: " + name);
                Ladder ladder = new Ladder(plugin, name);
                ladders.put(ladder.getName(), ladder);
            }
        }
        if(!ladders.containsKey(KiteMatch.KITE_LADDER_CHASER)){
            Ladder ladder = new Ladder(Practice.getPlugin(), KiteMatch.KITE_LADDER_CHASER, Material.ENDER_PEARL, false);
            ladder.register();
            ladder.save();
        }
        if(!ladders.containsKey(KiteMatch.KITE_LADDER_RUNNER)){
            Ladder ladder = new Ladder(Practice.getPlugin(), KiteMatch.KITE_LADDER_RUNNER, Material.ENDER_PEARL, false);
            ladder.register();
            ladder.save();
        }
    }

    public static Collection<Ladder> getLadders(){
        return Collections.unmodifiableCollection(ladders.values());
    }

    @ConfigData("name")
    private String name;

    @ConfigData("icon")
    @ConfigSerializer(serializer = MaterialSerializer.class)
    private Material icon;

    @ConfigData("editable")
    private boolean editable;

    @ConfigData("inventory")
    @ConfigSerializer(serializer = InventorySerializer.class)
    private Inventory inventory;

    @ConfigData("defaultKit")
    @ConfigSerializer(serializer = KitSerializer.class)
    private Kit defaultKit;

    public Ladder(Plugin plugin, String name, Material icon, boolean editable) {
        super(plugin, "ladders" + File.separator + name+".yml");
        this.name = name;
        this.icon = icon;
        this.editable = editable;
        this.inventory = Bukkit.createInventory(null,54, "Kit Creation");
        this.defaultKit = new Kit(name, new ItemStack[0], new ItemStack[0]);
        save();
    }

    public Ladder(Plugin plugin, String name) {
        super(plugin, "ladders" + File.separator + name + ".yml");
        load();
    }

    public void register(){
        ladders.put(name, this);
    }

    public void unregister(){
        if(ladders.containsKey(name)){
            ladders.remove(name);
        }
    }

}
