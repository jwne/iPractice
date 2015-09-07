package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.match.LadderSelect;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueType;
import com.shawckz.ipractice.util.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command(name = "rmatch", playerOnly = true)
public class CommandRMatch implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        final Player p = (Player) cmdArgs.getSender();
        final IPlayer ip = Practice.getCache().getIPlayer(p);

        if(ip.getState() != PlayerState.AT_SPAWN){
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
            return;
        }
        if(Queue.inQueue(ip.getName())){
            p.sendMessage(ChatColor.RED+"You cannot do this while you are in a queue.");
            return;
        }
        if(ip.getParty() != null){
            p.sendMessage(ChatColor.RED+"You cannot do this while you are in a party.");
            return;
        }

        new LadderSelect(ip){
            @Override
            public void onSelect(Ladder ladder) {
                Queue.addToQueue(ip, QueueType.RANKED, ladder);

                p.getInventory().clear();
                p.getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Leave Queue").build());
                p.updateInventory();
                p.getInventory().setContents(p.getInventory().getContents());
            }
        };

    }

}
