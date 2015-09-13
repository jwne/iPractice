package com.shawckz.ipractice.arena;

import com.shawckz.ipractice.Practice;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ArenaManager {

    public ArenaManager(Practice instance) {
        loadArenas(instance);
    }

    @Getter private final Set<Arena> arenas = new HashSet<>();

    public void loadArenas(Plugin plugin){
        {
            File dir = new File(plugin.getDataFolder(), "arenas");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String name = FilenameUtils.removeExtension(f.getName());
                    Arena arena = new BasicArena(plugin, name);
                    arenas.add(arena);
                    arenaIndex++;
                }
            }
        }
    }

    public void registerArena(Arena arena){
        if(!arenas.contains(arena)){
            arenas.add(arena);
        }
    }

    public void unregisterArena(Arena arena){
        arenas.remove(arena);
    }

    public Arena getArena(String name){
        for(Arena a : arenas){
            if(a.getName().equalsIgnoreCase(name)){
                return a;
            }
        }
        return null;
    }

    public Arena getArena(int id){
        for(Arena a : arenas){
            if(a.getId() == id){
                return a;
            }
        }
        return null;
    }

    @Getter @Setter private int arenaIndex = 0;

    public Arena getNextArena(){
        if(getArena(arenaIndex+1)!=null){
            arenaIndex++;
            return getArena(arenaIndex);
        }
        arenaIndex = 0;
        if(getArena(arenaIndex)!=null){
            return getArena(arenaIndex);
        }
        else{
            for(Arena arena : arenas){
                return arena;
            }
            return null;
        }
    }



}
