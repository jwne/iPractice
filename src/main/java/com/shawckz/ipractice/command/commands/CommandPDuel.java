package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.ladder.LadderSelect;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.party.PartyDuel;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "pduel", playerOnly = true, minArgs = 1, usage = "/pduel <player>")
public class CommandPDuel implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        final Player p = (Player) cmdArgs.getSender();
        final IPlayer ip = Practice.getCache().getIPlayer(p);

        if(ip.isStaffMode()){
            p.sendMessage(ChatColor.RED+"You cannot do this while in staff mode.");
            return;
        }
        if(ip.getState() != PlayerState.AT_SPAWN){
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
            return;
        }

        if(ip.getParty() != null){
            if(!ip.getParty().getLeader().equalsIgnoreCase(p.getName())){
                p.sendMessage(ChatColor.RED+"You are not the leader of the party.");
                return;
            }
            final Party party = ip.getParty();
            if(!party.canDuel()){
                p.sendMessage(ChatColor.RED+"One or more of your party members is not at spawn.");
                return;
            }
            final Player t = cmdArgs.getPlayer(0);
            if(t != null){
                final IPlayer tip = Practice.getCache().getIPlayer(t);
                if(tip.getParty() != null){
                    if(tip.getParty().getLeader().equalsIgnoreCase(t.getName())){
                        final Party targetParty = tip.getParty();
                        if(!targetParty.canDuel()){
                            p.sendMessage(ChatColor.RED+"One or more of that party's members are not at spawn.");
                            return;
                        }
                        if(targetParty.getLeader().equals(party.getLeader())){
                            p.sendMessage(ChatColor.RED+"You cannot duel your own party.");
                            return;
                        }
                        new LadderSelect(ip){
                            @Override
                            public void onSelect(Ladder ladder) {
                                targetParty.getDuels().add(new PartyDuel(party, targetParty, ladder));

                                party.msg(ChatColor.AQUA + "" + ChatColor.BOLD + "(PARTY) " + ChatColor.RESET + "" +
                                        ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GOLD + " has sent a duel request "
                                        + "to " + ChatColor.AQUA + tip.getName() + "'s Party " + ChatColor.GOLD + " with ladder "
                                        + ChatColor.AQUA + ladder.getName() + ChatColor.GOLD + ".");

                                {
                                    //Leader message
                                    FancyMessage fm = new FancyMessage("");
                                    fm.then((ChatColor.AQUA + "" + ChatColor.BOLD + "(PARTY) " + ChatColor.RESET + ""))
                                            .then(ChatColor.LIGHT_PURPLE+p.getName()+"'s Party "+ChatColor.GOLD+" has " +
                                                    "requested to duel you with "+ChatColor.AQUA+
                                                    ladder.getName()+ChatColor.GOLD+".");
                                    fm.send(t);
                                    new FancyMessage(ChatColor.GREEN+""+ChatColor.BOLD+" [CLICK HERE] "+ChatColor.GOLD+" to accept.")
                                            .tooltip(ChatColor.GOLD + "Accept duel request from " +
                                                    ChatColor.LIGHT_PURPLE + p.getName() + "'s Party")
                                            .command("/paccept " + p.getName())
                                    .send(t);
                                }
                                FancyMessage fm = new FancyMessage("");
                                fm.then((ChatColor.AQUA + "" + ChatColor.BOLD + "(PARTY) " + ChatColor.RESET + ""))
                                        .then(ChatColor.LIGHT_PURPLE + p.getName() + "'s Party " + ChatColor.GOLD + " has " +
                                                "requested to duel you with " + ChatColor.AQUA +
                                                ladder.getName() + ChatColor.GOLD + ".");
                                for(Player pl : targetParty.getAllPlayers()){
                                    if(!pl.getName().equalsIgnoreCase(targetParty.getLeader())){
                                        fm.send(pl);
                                    }
                                }
                            }
                        };
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"That player is not the leader of their party.");
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
            p.sendMessage(ChatColor.RED+"You are not in a party.");
        }

    }
}
