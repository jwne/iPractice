package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.kite.KiteMatch;
import com.shawckz.ipractice.kite.KiteRequest;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "kiteaccept", usage = "/kiteaccept <player>", minArgs = 1, playerOnly = true)
public class CommandKiteAccept implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        IPlayer ip = Practice.getCache().getIPlayer(p);
        if(ip.isStaffMode()){
            p.sendMessage(ChatColor.RED+"You cannot do this while in staff mode.");
            return;
        }
        if(ip.getState() == PlayerState.AT_SPAWN){
            if(Practice.getQueueManager().inQueue(ip)){
                p.sendMessage(ChatColor.RED+"You cannot do this while you are in a queue.");
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
                if(Practice.getQueueManager().inQueue(tip)){
                    p.sendMessage(ChatColor.RED+"That player is in a queue.");
                    return;
                }

                KiteRequest req = null;

                for(KiteRequest request : ip.getKiteRequests()){
                    if(request.getSender().getName().equalsIgnoreCase(t.getName())){
                        if(request.getExpiry() >= System.currentTimeMillis()){
                            req = request;
                            break;
                        }
                    }
                }

                if(req != null){
                    ip.getKiteRequests().remove(req);

                    KiteMatch match = new KiteMatch(Practice.getCache().getIPlayer(req.getRunner())
                                                    ,Practice.getCache().getIPlayer(req.getChaser()));
                    match.startMatch(Practice.getMatchManager());
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
