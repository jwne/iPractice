package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "watch", playerOnly = true, permission = "practice.staffmode", minArgs = 2, usage = "/watch <add|stop|clear> <player>")
public class CommandWatch implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        final String[] args = cmdArgs.getArgs();
        Player p = (Player) cmdArgs.getSender();
        IPlayer ip = Practice.getCache().getIPlayer(p);

        //watch <add|stop> <player>

        if(Practice.getQueueManager().inQueue(ip)){
            p.sendMessage(ChatColor.RED+"You cannot do this while you are in a queue.");
            return;
        }

        if(ip.getParty() != null){
            p.sendMessage(ChatColor.RED+"You cannot do this while you are in a party.");
            return;
        }

        if(!ip.isStaffMode() || ip.getState() != PlayerState.AT_SPAWN){
            p.sendMessage(ChatColor.RED+"You must be at spawn and in staff mode to watch a player.");
            return;
        }

        Player target = cmdArgs.getPlayer(1);
        if(target == null){
            p.sendMessage(ChatColor.RED+"Player '"+cmdArgs.getArg(1)+"' not found.");
            return;
        }

        if(args[0].equalsIgnoreCase("add")){
            if(!ip.getWatching().contains(target.getName())){
                ip.getWatching().add(target.getName());
                ip.handlePlayerVisibility();
                p.sendMessage(ChatColor.GREEN+"You are now watching "+target.getName()+".");
            }
            else{
                p.sendMessage(ChatColor.RED+"You are already watching "+target.getName()+".");
            }
        }
        else if (args[0].equalsIgnoreCase("stop")){
            if(ip.getWatching().contains(target.getName())){
                ip.getWatching().remove(target.getName());
                ip.handlePlayerVisibility();
                p.sendMessage(ChatColor.GREEN+"You are no longer watching "+target.getName()+".");
            }
            else{
                p.sendMessage(ChatColor.RED+"You are not watching "+target.getName()+".");
            }
        }
        else if (args[0].equalsIgnoreCase("clear")){
            ip.getWatching().clear();
            p.sendMessage(ChatColor.GREEN+"You are no longer watching any players.");
        }
        else{
            p.sendMessage(ChatColor.RED+"Incorrect usage.");
        }

    }
}
