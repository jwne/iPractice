package com.shawckz.ipractice.task;

import lombok.Getter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class AsyncBlockPlaceTask extends BukkitRunnable {

    private final ConcurrentMap<Location, Block> blocks;
    private final int bps;
    private final int totalBlocks;
    private int blockIndex = 0;
    private int blocksPlaced = 0;
    private boolean completed = false;
    private final Iterator<Location> iterator;

    public AsyncBlockPlaceTask(Map<Location, Block> blocks, int bps) {
        this.blocks = new ConcurrentHashMap<>();
        this.blocks.putAll(blocks);
        this.bps = bps;
        this.totalBlocks = blocks.keySet().size();
        this.iterator = blocks.keySet().iterator();
    }

    @Override
    public void run() {
        if(blocks.isEmpty() || !iterator.hasNext()){
            finish();
            completed = true;
            cancel();
            return;
        }
        while(iterator.hasNext()){
            if(blockIndex < bps) {
                Location loc = iterator.next();
                Block block = blocks.get(loc);

                if(!loc.getWorld().getChunkAt(loc).isLoaded()){
                    loc.getWorld().getChunkAt(loc).load();
                }

                loc.getBlock().setType(block.getType());
                loc.getBlock().setData(block.getData());
                loc.getBlock().getState().setType(block.getType());
                loc.getBlock().getState().setData(block.getState().getData());
                loc.getBlock().getState().update();

                blocks.remove(loc);
                blocksPlaced++;
                blockIndex++;
            }
            else{
                blockIndex = 0;
                /*for(Player pl : Bukkit.getOnlinePlayers()){
                    if(pl.isOp()){
                        pl.sendMessage(ChatColor.DARK_PURPLE+"Placed "+blocksPlaced+" of "+totalBlocks+" blocks ["+bps+" blocks/s]");
                    }
                }*/
                break;
            }
        }
    }

    public abstract void finish();

}
