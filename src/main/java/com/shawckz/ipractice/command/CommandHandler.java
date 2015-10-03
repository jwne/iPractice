package com.shawckz.ipractice.command;

import com.shawckz.ipractice.command.commands.*;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 360 on 5/30/2015.
 */

/**
 * The CommandHandler object
 * Used to register a set of commands and handle them within a JavaPlugin
 * ***Requires adding the commands to your plugin.yml***
 */
public class CommandHandler implements CommandExecutor {

    @Getter
    private List<ICommand> commands;
    private JavaPlugin javaPlugin;

    public CommandHandler(JavaPlugin javaPlugin) {
        this.commands = new ArrayList<>();
        this.javaPlugin = javaPlugin;

        registerCommand(new CommandPractice(),true);
        registerCommand(new CommandDuel(), true);
        registerCommand(new CommandAccept(), true);
        registerCommand(new CommandViewInventory(), true);
        registerCommand(new CommandParty(), true);
        registerCommand(new CommandPDuel(), true);
        registerCommand(new CommandPAccept(), true);
        registerCommand(new CommandKiteAccept(), true);
        registerCommand(new CommandResetElo(), true);
        registerCommand(new CommandSave(), true);
        registerCommand(new CommandStaffMode(), true);
        registerCommand(new CommandWatch(), true);
    }

    private void registerCommand(ICommand cmd,boolean single){
        if(!commands.contains(cmd)) {
            commands.add(cmd);
            if(single){
                if (cmd.getClass().isAnnotationPresent(Command.class)) {
                    Command command = cmd.getClass().getAnnotation(Command.class);
                    javaPlugin.getCommand(command.name()).setExecutor(this);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String s, String[] args) {
        for(ICommand pCmd : commands){
            if(pCmd.getClass().isAnnotationPresent(Command.class)){
                Command command = pCmd.getClass().getAnnotation(Command.class);
                if(command.name().equalsIgnoreCase(cmd.getName())) {
                    if (!sender.hasPermission(command.permission()) && !command.permission().equals("")) {
                        if(!command.noPerm().equals("")){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', command.noPerm()));
                        }
                        else{
                            sender.sendMessage(ChatColor.RED+"No permission.");
                        }
                        return true;
                    }
                    if (args.length < command.minArgs()) {
                        sender.sendMessage(ChatColor.RED + "Usage: " + command.usage());
                        return true;
                    }
                    if (command.playerOnly() && !(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This is a player only command.");
                        return true;
                    }
                    pCmd.onCommand(new CmdArgs(sender, args));
                    return true;
                }
            }
        }
        return true;
    }
}
