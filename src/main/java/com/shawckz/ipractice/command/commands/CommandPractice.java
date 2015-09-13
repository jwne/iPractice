package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.arena.BasicArena;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.match.Ladder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Command(name = "practice", usage = "/practice", minArgs = 0, playerOnly = true, permission = "practice.admin")
public class CommandPractice implements ICommand {

    @Override
    public void onCommand(CmdArgs cmdArgs) {
        Player p = (Player) cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if(args.length == 0){
            p.sendMessage(ChatColor.AQUA+"Practice Help");
            p.sendMessage(ChatColor.YELLOW+"/practice addladder <name> <icon(material)> <editable(true/false)>");
            p.sendMessage(ChatColor.YELLOW+"/practice delladder <name>");
            p.sendMessage(ChatColor.YELLOW+"/practice setspawn");
            p.sendMessage(ChatColor.YELLOW+"/practice setkitspawn");
            p.sendMessage(ChatColor.YELLOW+"/practice setkitinv <ladder>");
            p.sendMessage(ChatColor.YELLOW+"/practice setdefaultkit <ladder>");
            p.sendMessage(ChatColor.YELLOW+"/practice createarena <arena name>");
            p.sendMessage(ChatColor.YELLOW+"/practice delarena <arena name>");
            p.sendMessage(ChatColor.YELLOW+"/practice setarenaspawn <arena name> <a|b>");
            p.sendMessage(ChatColor.YELLOW+"/practice listarenas");
            return;
        }

        String key = args[0];

        if(key.equalsIgnoreCase("addladder")){
            if(args.length >= 4){
                String name = args[1];
                name = name.replaceAll("_", " ");
                Material icon;
                try{
                    icon = Material.valueOf(args[2].toUpperCase());
                }
                catch (Exception excpected){
                    p.sendMessage(ChatColor.RED+"Invalid Material");
                    return;
                }
                boolean editable;
                try{
                    editable = Boolean.parseBoolean(args[3]);
                }
                catch (Exception expected){
                    p.sendMessage(ChatColor.RED+"Invalid boolean");
                    return;
                }

                Ladder ladder = new Ladder(Practice.getPlugin(), name, icon, editable);
                ladder.register();
                p.sendMessage(ChatColor.GREEN+"Ladder '"+name+"' created.");
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("delladder")){
            if(args.length >= 2){
                Ladder ladder = Ladder.getLadder(args[1]);
                if(ladder != null){
                    ladder.unregister();
                    ladder.deleteFile();
                    p.sendMessage(ChatColor.GREEN + "Ladder '" + ladder.getName() + "' deleted.");
                }
                else{
                    p.sendMessage(ChatColor.RED+"A ladder by that name does not exist. (case sensitive)");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("setspawn")){
            Practice.getIConfig().setSpawn(p.getLocation());
            Practice.getIConfig().save();
            p.sendMessage(ChatColor.GREEN+"Spawn set to your location.");
        }
        else if (key.equalsIgnoreCase("setkitspawn")){
            Practice.getIConfig().setKitBuilderSpawn(p.getLocation());
            Practice.getIConfig().save();
            p.sendMessage(ChatColor.GREEN + "KitBuilder Spawn set to your location.");
        }
        else if (key.equalsIgnoreCase("setkitinv")){
            if(args.length >= 2){
                Ladder ladder = Ladder.getLadder(args[1]);
                if(ladder != null){
                    Inventory inv = Bukkit.createInventory(null,54,ladder.getName());
                    inv.setContents(ladder.getInventory().getContents());
                    p.openInventory(inv);
                }
                else{
                    p.sendMessage(ChatColor.RED+"A ladder by that name does not exist. (case sensitive)");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("setdefaultkit")){
            if(args.length >= 2){
                Ladder ladder = Ladder.getLadder(args[1]);
                if(ladder != null){
                    ladder.getDefaultKit().setArmor(p.getInventory().getArmorContents());
                    ladder.getDefaultKit().setInventory(p.getInventory().getContents());
                    ladder.save();
                    p.sendMessage(ChatColor.GREEN+"Updated the default kit for ladder: "+ladder.getName()+
                            " to match your inventory.");
                }
                else{
                    p.sendMessage(ChatColor.RED+"A ladder by that name does not exist. (case sensitive)");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("createarena")){
            if(args.length >= 2){
                if(Practice.getArenaManager().getArena(args[1].replaceAll("_", " ")) != null){
                    p.sendMessage(ChatColor.RED+"An arena by that name already exists.");
                    return;
                }
                String name = args[1];
                name = name.replaceAll("_"," ");

                BasicArena arena = new BasicArena(Practice.getPlugin(), name, p.getLocation(), p.getLocation());
                Practice.getArenaManager().registerArena(arena);
                arena.save();
                p.sendMessage(ChatColor.GREEN+"Created arena '"+name+"'.  Set it's spawns with /prac setarenaspawn.");
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("delarena")){
            if(args.length >= 2){
                if(Practice.getArenaManager().getArena(args[1].replaceAll("_", " ")) == null){
                    p.sendMessage(ChatColor.RED+"An arena by that name does not exist.");
                    return;
                }
                String name = args[1];
                name = name.replaceAll("_"," ");

                Arena arena = Practice.getArenaManager().getArena(name);
                Practice.getArenaManager().unregisterArena(arena);
                arena.deleteFile();
                p.sendMessage(ChatColor.GREEN+"Deleted arena '"+name+"'.");
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("setarenaspawn")){
            if(args.length >= 3){
                if(Practice.getArenaManager().getArena(args[1].replaceAll("_", " ")) == null){
                    p.sendMessage(ChatColor.RED+"An arena by that name does not exist.");
                    return;
                }
                String name = args[1];
                name = name.replaceAll("_"," ");

                Arena arena = Practice.getArenaManager().getArena(name);

                if(args[2].equalsIgnoreCase("a")){
                    arena.setSpawnAlpha(p.getLocation());
                    arena.save();
                    p.sendMessage(ChatColor.GREEN+"Set spawn ALPHA for arena '"+arena.getName()+"'.");
                }
                else if (args[2].equalsIgnoreCase("b")){
                    arena.setSpawnBravo(p.getLocation());
                    arena.save();
                    p.sendMessage(ChatColor.GREEN + "Set spawn BRAVO for arena '" + arena.getName() + "'.");
                }
                else{
                    p.sendMessage(ChatColor.RED+"Incorrect usage.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("listarenas")){
            p.sendMessage(ChatColor.GOLD+"Registered arenas:");
            if(Practice.getArenaManager().getArenas().isEmpty()){
                p.sendMessage(ChatColor.GRAY+" - "+ChatColor.AQUA+"(There are no registered arenas)");
            }
            else{
                for(Arena arena : Practice.getArenaManager().getArenas()){
                    p.sendMessage(ChatColor.GRAY+" - "+ChatColor.AQUA+arena.getName());
                }
            }
        }
        else{
            onCommand(new CmdArgs(p, new String[0]));
        }


    }
}
