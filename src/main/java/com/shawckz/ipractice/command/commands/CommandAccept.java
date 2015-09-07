package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.DuelRequest;
import com.shawckz.ipractice.match.type.BasicMatch;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "accept", usage = "/accept <player>", minArgs = 1, playerOnly = true)
public class CommandAccept implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.getState() == PlayerState.AT_SPAWN){
            if(Queue.inQueue(ip.getName())){
                p.sendMessage(ChatColor.RED+"You cannot duel this while you are in a queue.");
                return;
            }
            Player t = cmdArgs.getPlayer(0);
            if(t != null){
                if(t.getName().equalsIgnoreCase(p.getName())){
                    p.sendMessage(ChatColor.RED+"You cannot duel yourself.");
                    return;
                }
                IPlayer tip = Practice.getCache().getIPlayer(t);
                if(tip.getState() != PlayerState.AT_SPAWN){
                    p.sendMessage(ChatColor.RED+"That player is not at spawn.");
                    return;
                }
                if(tip.getParty() != null){
                    p.sendMessage(ChatColor.RED+"That player is in a party.");
                    return;
                }
                if(Queue.inQueue(tip.getName())){
                    p.sendMessage(ChatColor.RED+"That player is in a queue.");
                    return;
                }

                DuelRequest req = null;

                for(DuelRequest request : ip.getDuelRequests()){
                    if(request.getSender().getName().equalsIgnoreCase(t.getName())){
                        if(request.getExpiry() >= System.currentTimeMillis()){
                            req = request;
                            break;
                        }
                    }
                }

                if(req != null){
                    ip.getDuelRequests().remove(req);
                    BasicMatch match = new BasicMatch(req.getLadder(), req.getSender().getPlayer(),
                            req.getRecipient().getPlayer(), false);
                    match.start();
                }
                else{
                    p.sendMessage(ChatColor.RED+"You do not have a pending duel request from that player.");
                }

            }
            else{
                p.sendMessage(ChatColor.RED+"Could not find player '"+cmdArgs.getArg(0)+"'.");
            }
        }
        else{
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
        }
    }
}
