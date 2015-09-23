package com.shawckz.ipractice.task;

import com.shawckz.ipractice.arena.Arena;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 360 on 9/22/2015.
 */
@Getter
@RequiredArgsConstructor
public abstract class ArenaDupeTask implements Runnable {

    private final Arena copiedArena;
    private final int offsetX;
    private final int offsetZ;

    public void run() {
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.isOp()){
                pl.sendMessage(ChatColor.DARK_PURPLE+"Preparing to duplicate arena "+copiedArena.getName()+"...");
            }
        }
        List<Block> copy = blocksFromTwoPoints(copiedArena.getMin(), copiedArena.getMax());
        List<Block> paste = new ArrayList<>();
        for(Block block : copy){
            paste.add(block.getLocation().getWorld().getBlockAt(
                    block.getLocation().clone().add(
                            offsetX,//X
                            0,//Y
                            offsetZ//Z
                    )
            ));
        }
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.isOp()){
                pl.sendMessage(ChatColor.DARK_PURPLE+"Scanning arena "+copiedArena.getName()+" to see if paste is safe...");
            }
        }
        boolean safe = true;
        for(Block block : paste){
            if(block.getWorld().getBlockAt(block.getLocation()).getType() != Material.AIR){
                safe = false;
                break;
            }
        }
        if(safe){
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(pl.isOp()){
                    pl.sendMessage(ChatColor.DARK_PURPLE+"Arena scan complete: Paste is safe.");
                }
            }
        }
        else{
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(pl.isOp()){
                    pl.sendMessage(ChatColor.DARK_PURPLE+"Arena scan complete: Paste is not safe - Conflicting non-air blocks found in area.");
                }
            }
            return;
        }
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.isOp()){
                pl.sendMessage(ChatColor.DARK_PURPLE+"Duplicating arena "+copiedArena.getName()+"... Expect lag.");
            }
        }
        int blocksPlaced = 0;
        for(Block block : paste){
            block.getWorld().getBlockAt(block.getLocation()).setType(block.getType());
            block.getWorld().getBlockAt(block.getLocation()).getState().setType(block.getType());
            blocksPlaced++;
        }
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.isOp()){
                pl.sendMessage(ChatColor.DARK_PURPLE+"Duplicated arena "+copiedArena.getName()+".  " +
                        "Placed "+blocksPlaced+" blocks.");
            }
        }
        onComplete();
    }

    public List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<>();

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
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public abstract void onComplete();

}
