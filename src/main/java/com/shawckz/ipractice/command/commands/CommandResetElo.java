package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.player.ICache;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by 360 on 9/18/2015.
 */

@Command(name = "resetelo", usage = "/resetelo <player> <ladder|all>", minArgs = 2, playerOnly = false, permission = "practice.resetelo")
public class CommandResetElo implements ICommand {
    @Override
    public void onCommand(final CmdArgs cmdArgs) {
        cmdArgs.getSender().sendMessage(ChatColor.GRAY+"Searching database asynchronously...");
        new BukkitRunnable(){
            @Override
            public void run() {
                CommandSender s = cmdArgs.getSender();
                String player = cmdArgs.matchPlayer(0);
                IPlayer target = Practice.getCache().getIPlayer(player);
                if(target != null){
                    if(cmdArgs.getArg(1).equalsIgnoreCase("all")){
                        for(Ladder ladder : Ladder.getLadders()){
                            target.setElo(ladder, IPlayer.DEFAULT_ELO);
                        }
                        target.update();
                        Player t = Bukkit.getPlayer(player);
                        if(t != null){
                            if(target.getState() == PlayerState.AT_SPAWN){
                                target.getScoreboard().update();
                            }
                            t.sendMessage(ChatColor.GREEN+"Your elo has been reset to "+IPlayer.DEFAULT_ELO+" for all ladders.");
                        }
                        s.sendMessage(ChatColor.GREEN + "You reset " + player + "'s elo to " + IPlayer.DEFAULT_ELO + " for all ladders.");

                    }
                    else{
                        Ladder ladder = Ladder.getLadder(cmdArgs.getArg(1));
                        if(ladder != null){
                            target.setElo(ladder, IPlayer.DEFAULT_ELO);
                            target.update();
                            Player t = Bukkit.getPlayer(player);
                            if(t != null){
                                if(target.getState() == PlayerState.AT_SPAWN){
                                    target.getScoreboard().update();
                                }
                                t.sendMessage(ChatColor.GREEN+"Your elo has been reset to "+IPlayer.DEFAULT_ELO+" for ladder: "+ladder.getName()+".");
                            }
                            s.sendMessage(ChatColor.GREEN+"You reset "+player+"'s elo to "+IPlayer.DEFAULT_ELO+" for ladder: "+ladder.getName()+".");
                        }
                        else{
                            s.sendMessage(ChatColor.RED+"Ladder '"+cmdArgs.getArg(1)+"' does not exist.");
                        }
                    }
                }
                else{
                    s.sendMessage(ChatColor.RED+"Player '"+player+"' does not exist in database. (case sensitive)");
                }
            }
        }.runTaskAsynchronously(Practice.getPlugin());
    }
}
