package com.shawckz.ipractice.arena;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.task.ArenaDupeTask;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class ArenaManager {

    public static final Random RANDOM = new Random();

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
                    Arena arena = new BasicArena(plugin, Integer.parseInt(name));
                    arenas.add(arena);
                    arenaIndex++;
                }
            }
        }
        {
            File dir = new File(plugin.getDataFolder(), "kitearenas");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String name = FilenameUtils.removeExtension(f.getName());
                    KiteArena arena = new KiteArena(plugin, Integer.parseInt(name));
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

    public int getNextArenaIndex(){
        int i = ++arenaIndex;
        while(getArena(i) != null){
            i++;
        }
        return i;
    }

    public Arena getNextArena(){
        for(Arena arena : arenas){
            if(arena != null && !arena.isHasMatch()){
                return arena;
            }
        }
        return null;
    }

    public Arena getNextArena(ArenaType type){
        for(Arena arena : arenas){
            if(arena != null && !arena.isHasMatch() && arena.getType() == type){
                return arena;
            }
        }
        return null;
    }

    public Arena getRandomArena(ArenaType type){
        int x = RANDOM.nextInt(arenas.size());
        int i = 0;
        for(Arena arena : arenas){
            if(arena.getType() == type &&
                    i >= x){
                return arena;
            }
            i++;
        }
        //if that didn't work just get the first one we can
        for(Arena arena : arenas){
            return arena;
        }
        return null;
    }

    public Arena getNewestArena(ArenaType type){
        int x = 0;
        Arena a = null;
        List<String> arenaNames = new ArrayList<>();
        for(Arena arena : arenas){
            if(!arenaNames.contains(arena.getName())){
                arenaNames.add(arena.getName());
            }
        }
        if(arenaNames.isEmpty()){
            return null;
        }
        String arenaName = arenaNames.get(RANDOM.nextInt(arenaNames.size()));
        for(Arena arena : arenas){
            if(arena.getId() > x && arena.getType() == type && arena.getName().equalsIgnoreCase(arenaName)){
                x = arena.getId();
                a = arena;
            }
        }
        return a;
    }

}
