package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.party.PartyDuel;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "paccept", playerOnly = true, minArgs = 1, usage = "/paccept <player>")
public class CommandPAccept implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        IPlayer ip = Practice.getCache().getIPlayer(p);

        if(ip.getState() != PlayerState.AT_SPAWN){
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
            return;
        }

        if(ip.getParty() != null){
            if(ip.getParty().canDuel()){
                Party party = ip.getParty();
                final Player t = cmdArgs.getPlayer(0);
                if(t != null){
                    IPlayer tip = Practice.getCache().getIPlayer(t);
                    if(tip.getParty() != null){
                        Party tParty = tip.getParty();
                        if(tParty.canDuel()){
                            if(party.hasDuel(tParty)){
                                PartyDuel duel = party.getDuel(tParty);

                                Match match = Practice.getMatchManager().matchBuilder(duel.getLadder())
                                        .registerTeam(new PracticeTeam(duel.getSender().getLeader()+"'s Party", Team.ALPHA))
                                        .registerTeam(new PracticeTeam(duel.getRecipient().getLeader()+"'s Party", Team.BRAVO))
                                        .withParty(duel.getSender(), duel.getSender().getLeader()+"'s Party")
                                        .withParty(duel.getRecipient(), duel.getRecipient().getLeader()+"'s Party")
                                        .setRanked(false)
                                        .build();

                                match.startMatch(Practice.getMatchManager());

                            }
                            else{
                                p.sendMessage(ChatColor.RED+"That party has not challenged you to a duel.");
                            }
                        }
                        else{
                            p.sendMessage(ChatColor.RED+"One of more of that party's members are not at spawn.");
                        }
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"That player is not in a party.");
                    }
                }
                else{
                    p.sendMessage(ChatColor.RED+"Could not find player '"+cmdArgs.getArg(0)+"'.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"One or more of your party members is not at spawn.");
            }
        }
        else{
            p.sendMessage(ChatColor.RED+"You are not in a party.");
        }


    }
}
