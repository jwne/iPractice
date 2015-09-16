package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.MatchInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Command(name = "viewinv", playerOnly = true, minArgs = 1)
public class CommandViewInventory implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        String id = cmdArgs.getArg(0);
        if(MatchInventory.getMatchInventories().containsKey(id)){

            MatchInventory.getMatchInventory(id).open(p);

        }
        else{
            p.sendMessage(ChatColor.RED+"Inventory by that ID not found.");
        }
    }
}
