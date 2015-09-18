package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import org.bukkit.ChatColor;

/**
 * Created by 360 on 9/18/2015.
 */
@Command(name = "pforcesave", usage = "/pforcesave", permission = "practice.save")
public class CommandSave implements ICommand{

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Practice.getTaskAutoSave().run();
        cmdArgs.getSender().sendMessage(ChatColor.GREEN+"Save task forcefully run.");
    }
}
