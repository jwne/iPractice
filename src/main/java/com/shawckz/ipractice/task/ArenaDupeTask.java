package com.shawckz.ipractice.task;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 360 on 9/22/2015.
 */
@Getter
public abstract class ArenaDupeTask implements Runnable {

    private final Arena copiedArena;
    private int offsetX;
    private int offsetZ;
    private final int maxTries;
    private final int incrementX;
    private final int incrementZ;
    private boolean successful = false;
    private boolean completed = false;
    int tries = 1;

    public ArenaDupeTask(Arena copiedArena, int offsetX, int offsetZ, int maxTries, int incrementX, int incrementZ) {
        this.copiedArena = copiedArena;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.maxTries = maxTries;
        this.incrementX = incrementX;
        this.incrementZ = incrementZ;
    }

    public void run() {
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.isOp()){
                pl.sendMessage(ChatColor.DARK_PURPLE+"Preparing to duplicate arena "+copiedArena.getName()+"...");
            }
        }
        Map<Location,Block> copy = blocksFromTwoPoints(copiedArena.getMin(), copiedArena.getMax());
        Map<Location,Block> paste = new HashMap<>();
        for(Location loc : copy.keySet()){
            if(copy.get(loc).getType() != Material.AIR) {
                paste.put(
                        loc.clone().add(offsetX, 0, offsetZ),
                        copy.get(loc)
                );
            }
        }
        boolean safe = true;
        for(Location loc : paste.keySet()){
            Block block = loc.getBlock();
            if(block.getType() != Material.AIR){
                safe = false;
                break;
            }
        }
        if(!safe){
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(pl.isOp()){
                    pl.sendMessage(ChatColor.DARK_PURPLE+"Arena scan complete:"+ChatColor.RED+" Paste is not safe - Incrementing offset and re-trying. ("+tries+"/"+maxTries+")");
                }
            }
            if(tries >= maxTries){
                for(Player pl : Bukkit.getOnlinePlayers()){
                    if(pl.isOp()){
                        pl.sendMessage(ChatColor.DARK_PURPLE+"Arena scan complete:"+ChatColor.RED+" Paste is not safe.");
                    }
                }
                completed = true;
                return;
            }
            else{
                tries++;
                offsetX += incrementX;
                offsetZ += incrementZ;
                run();
            }
            completed = true;
            return;
        }
        final AsyncBlockPlaceTask task = new AsyncBlockPlaceTask(paste, 50) {
            @Override
            public void finish() {
                for(Player pl : Bukkit.getOnlinePlayers()){
                    if(pl.isOp()){
                        pl.sendMessage(ChatColor.DARK_PURPLE+"Duplicated arena "+copiedArena.getName()+".  " +
                                "Placed "+getBlocksPlaced()+" blocks.");
                    }
                }
                completed = true;
                successful = true;
                onComplete(copiedArena.duplicate(offsetX, offsetZ));
            }
        };
        task.runTaskTimer(Practice.getPlugin(), 0L, 20L);//Sync
        completed = true;
    }

    public Map<Location, Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        Map<Location, Block> blocks = new HashMap<>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.put(new Location(loc1.getWorld(),x,y,z),block);
                }
            }
        }

        return blocks;
    }

    public abstract void onComplete(Arena result);

}
