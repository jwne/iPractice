package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by 360 on 9/7/2015.
 */
@Command(name = "staffmode", playerOnly = true, permission = "practice.staffmode")
public class CommandStaffMode implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        IPlayer ip = Practice.getCache().getIPlayer(p);

        if(ip.getState() != PlayerState.AT_SPAWN){
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
            return;
        }

        if(Practice.getQueueManager().inQueue(ip)){
            p.sendMessage(ChatColor.RED+"You cannot do this while you are in a queue.");
            return;
        }

        if(ip.getParty() != null){
            p.sendMessage(ChatColor.RED+"You cannot do this while you are in a party.");
            return;
        }

        ip.setStaffMode(!ip.isStaffMode());

        p.sendMessage(ChatColor.BLUE+"Staff mode "+(ip.isStaffMode() ? "enabled" : "disabled") + ".");
    }
}
