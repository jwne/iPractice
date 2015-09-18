package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.DuelRequest;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.ladder.LadderSelect;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "duel", playerOnly = true, minArgs = 1, usage = "/duel <player>")
public class CommandDuel implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        final IPlayer ip = Practice.getCache().getIPlayer(p);

        if(ip.isStaffMode()){
            p.sendMessage(ChatColor.RED+"You cannot do this while in staff mode.");
            return;
        }

        if(ip.getState() == PlayerState.AT_SPAWN){
            if(ip.getDuelRequestCooldown() < System.currentTimeMillis()){

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
                    if(ip.getParty() != null){
                        p.sendMessage(ChatColor.RED+"You cannot do this while you are in a party.");
                        return;
                    }
                    final IPlayer tip = Practice.getCache().getIPlayer(t);
                    if(tip.isStaffMode()){
                        p.sendMessage(ChatColor.RED+"Could not find player '"+cmdArgs.getArg(0)+"'.");
                        return;
                    }
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

                    new LadderSelect(ip){
                        @Override
                        public void onSelect(Ladder ladder) {
                            DuelRequest duelRequest = new DuelRequest(ladder, ip, tip, System.currentTimeMillis());
                            duelRequest.send();
                        }
                    };

                }
                else{
                    p.sendMessage(ChatColor.RED+"Could not find player '"+cmdArgs.getArg(0)+"'.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Slow down.  You can send another duel request in "+
                        ((ip.getDuelRequestCooldown()-System.currentTimeMillis())/1000)+" seconds.");
            }
        }
        else{
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
        }

    }

}
