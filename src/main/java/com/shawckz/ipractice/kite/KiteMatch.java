package com.shawckz.ipractice.kite;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.player.IPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
@Getter
public class KiteMatch implements PracticeMatch{

    private final IPlayer runner;
    private final IPlayer chaser;
    private boolean started = false;
    private boolean over = false;
    private int runnerCountdown = 5;
    private int chaserCountdown = 10;
    private KiteMatchHandler matchHandler;

    @Override
    public void startMatch(){
        if(started){
            throw new PracticeException("Attempted to start KiteMatch when already running");
        }

        matchHandler = new KiteMatchHandler(this);
        matchHandler.register();

        new BukkitRunnable(){
            @Override
            public void run() {
                if(runnerCountdown > 0){
                    runnerCountdown--;
                    runner.getPlayer().sendMessage(ChatColor.GOLD + "Start Kiting in " + ChatColor.LIGHT_PURPLE + runnerCountdown + ChatColor.GOLD + "...");
                }
                else{
                    runner.getPlayer().sendMessage(ChatColor.GREEN + "Go!");
                }

                if(chaserCountdown > 0){
                    chaserCountdown--;
                    chaser.getPlayer().sendMessage(ChatColor.GOLD + "You can start chasing in " + ChatColor.LIGHT_PURPLE + chaserCountdown + ChatColor.GOLD + "...");
                }
                else{
                    chaser.getPlayer().sendMessage(ChatColor.GREEN + "Go!");
                    started = true;
                }

            }
        }.runTaskTimer(Practice.getPlugin(),20L,20L);
    }

    public void msg(String msg){
        runner.getPlayer().sendMessage(msg);
        chaser.getPlayer().sendMessage(msg);
    }

    @Override
    public void endMatch() {
        if(!started || over){
            throw new PracticeException("Attempted to end KiteMatch when already over or not started (started:"+started+",over:"+over+")");
        }
        matchHandler.unregister();
        started = false;
        over = true;

        if(runner != null){
            runner.sendToSpawn();
        }
        if(chaser != null){
            chaser.sendToSpawn();
        }
    }

    public void eliminatePlayer(Player player){
        if(runner.getName().equals(player.getName())){
            msg(ChatColor.LIGHT_PURPLE+chaser.getName()+ChatColor.GOLD+" has won!");
            endMatch();
        }
        else if (chaser.getName().equals(player.getName())){
            msg(ChatColor.LIGHT_PURPLE+runner.getName()+ChatColor.GOLD+" has won!");
            endMatch();
        }
        else{
            throw new PracticeException("Not in KiteMatch?: "+player.getName());
        }
    }

    public boolean contains(Player player){
        return chaser.getPlayer().getName().equals(player.getName()) ||
                runner.getPlayer().getName().equals(player.getName());
    }

    public KiteRole getRole(Player player){
        if(contains(player)){
            if(chaser.getPlayer().getName().equals(player.getName())){
                return KiteRole.CHASER;
            }
            else{
                return KiteRole.RUNNER;
            }
        }
        return null;
    }

    @Override
    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        players.add(runner.getPlayer());
        players.add(chaser.getPlayer());
        return players;
    }
}
