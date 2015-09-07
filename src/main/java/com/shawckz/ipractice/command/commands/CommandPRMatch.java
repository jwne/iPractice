package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.match.LadderSelect;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.util.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command(name = "prmatch", playerOnly = true)
public class CommandPRMatch implements ICommand{

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        final Player p = (Player) cmdArgs.getSender();
        final IPlayer ip = Practice.getCache().getIPlayer(p);

        if(ip.getState() != PlayerState.AT_SPAWN){
            p.sendMessage(ChatColor.RED+"You are not at spawn.");
            return;
        }

        if(ip.getParty() != null){
            final Party party = ip.getParty();

            if(party.getAllMembers().toArray().length != 2) {
                p.sendMessage(ChatColor.RED+"You must have 2 members in your party to join the 2v2 ranked party queue.");
                return;
            }

            if(party.getLeader().equalsIgnoreCase(p.getName())){
                if(!Queue.inPartyQueue(party) && !Queue.inRankedPartyQueue(party)){
                    if(!party.canDuel()){
                        p.sendMessage(ChatColor.RED+"One or more of your party members is not at spawn.");
                        return;
                    }

                    new LadderSelect(ip){
                        @Override
                        public void onSelect(Ladder ladder) {
                            Queue.addToRankedPartyQueue(party, ladder);
                            p.getInventory().clear();
                            p.getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Leave Queue").build());
                            p.updateInventory();
                            p.getInventory().setContents(p.getInventory().getContents());
                        }
                    };

                }
                else{
                    p.sendMessage(ChatColor.RED+"Your party is already in the party queue.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Only the party leader can do this.");
            }
        }
        else{
            p.sendMessage(ChatColor.RED+"You are not in a party.");
        }

    }
}
