package com.shawckz.ipractice.command.commands;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.arena.ArenaType;
import com.shawckz.ipractice.arena.BasicArena;
import com.shawckz.ipractice.arena.KiteArena;
import com.shawckz.ipractice.command.CmdArgs;
import com.shawckz.ipractice.command.Command;
import com.shawckz.ipractice.command.ICommand;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.task.ArenaDupeTask;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Command(name = "practice", usage = "/practice", minArgs = 0, playerOnly = true, permission = "practice.admin")
public class CommandPractice implements ICommand {

    @Override
    public void onCommand(final CmdArgs cmdArgs) {
        final Player p = (Player) cmdArgs.getSender();
        final String[] args = cmdArgs.getArgs();

        if(args.length == 0){
            p.sendMessage(ChatColor.AQUA+"Practice Help");
            p.sendMessage(ChatColor.GRAY+"*** Ladders ***");
            p.sendMessage(ChatColor.YELLOW+"/practice addladder <name> <icon(material)> <editable(true/false)>");
            p.sendMessage(ChatColor.YELLOW+"/practice delladder <id>");
            p.sendMessage(ChatColor.GRAY+"*** Spawns ***");
            p.sendMessage(ChatColor.YELLOW+"/practice setspawn");
            p.sendMessage(ChatColor.YELLOW+"/practice setkitspawn");
            p.sendMessage(ChatColor.GRAY+"*** Kits ***");
            p.sendMessage(ChatColor.YELLOW+"/practice setkitinv <ladder>");
            p.sendMessage(ChatColor.YELLOW+"/practice setdefaultkit <ladder>");
            p.sendMessage(ChatColor.GRAY+"*** Arenas ***");
            p.sendMessage(ChatColor.YELLOW+"/practice createarena <arena name>");
            p.sendMessage(ChatColor.YELLOW+"/practice delarena <arena name>");
            p.sendMessage(ChatColor.YELLOW+"/practice setarenaspawn <id> <a|b>");
            p.sendMessage(ChatColor.YELLOW+"/practice listarenas");
            p.sendMessage(ChatColor.YELLOW+"/practice dupearena <id> <offsetX> <offsetZ>");
            p.sendMessage(ChatColor.GRAY+"*** Kite Arenas ***");
            p.sendMessage(ChatColor.YELLOW+"/practice createkitearena <arena name>");
            p.sendMessage(ChatColor.YELLOW+"/practice setkitearenaspawn <id> <runner|chaser|end>");
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
            Practice.getIConfig().setSpawn(p.getLocation().getBlock().getLocation());
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
                String name = args[1];
                name = name.replaceAll("_"," ");

                Selection selection = Practice.getWorldEdit().getSelection(p);
                if(selection != null) {
                    BasicArena arena = new BasicArena(Practice.getPlugin(), Practice.getArenaManager().getNextArenaIndex(), name, p.getLocation(), p.getLocation(),
                            selection.getMinimumPoint(), selection.getMaximumPoint());
                    Practice.getArenaManager().registerArena(arena);
                    arena.save();
                    p.sendMessage(ChatColor.GREEN + "Created arena "+arena.getId()+" '" + name + "'.  Set it's spawns with /prac setarenaspawn.");
                }
                else{
                    p.sendMessage(ChatColor.RED+"Make a WorldEdit selection around the arena first.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("delarena")){
            if(args.length >= 2){
                try {
                    int id = Integer.parseInt(args[1]);

                    Arena arena = Practice.getArenaManager().getArena(id);
                    if(arena != null) {
                        Practice.getArenaManager().unregisterArena(arena);
                        arena.deleteFile();
                        p.sendMessage(ChatColor.GREEN + "Deleted arena " + arena.getId() + " '" + arena.getName() + "'.");
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"Arena by ID "+id+" does not exist.");
                    }
                }
                catch (NumberFormatException expected){
                    p.sendMessage(ChatColor.RED+"The ID must be a number.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("setarenaspawn")){
            if(args.length >= 3){
                try {
                    int id = Integer.parseInt(args[1]);

                    Arena arena = Practice.getArenaManager().getArena(id);

                    if(arena != null) {

                        if (args[2].equalsIgnoreCase("a")) {
                            arena.setSpawnAlpha(p.getLocation());
                            arena.save();
                            p.sendMessage(ChatColor.GREEN + "Set spawn ALPHA for arena ID " + arena.getId() + " '" + arena.getName() + "'.");
                        } else if (args[2].equalsIgnoreCase("b")) {
                            arena.setSpawnBravo(p.getLocation());
                            arena.save();
                            p.sendMessage(ChatColor.GREEN + "Set spawn BRAVO for arena ID " + arena.getId() + " '" + arena.getName() + "'.");
                        } else {
                            p.sendMessage(ChatColor.RED + "Incorrect usage.");
                        }
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"Arena by ID "+id+" does not exist.");
                    }
                }
                catch (NumberFormatException expected){
                    p.sendMessage(ChatColor.RED+"The arena ID must be a number.");
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
                    p.sendMessage(ChatColor.GRAY+" - "+ChatColor.AQUA+"#"+arena.getId()+" "+arena.getName() + ChatColor.GREEN+" ("+arena.getType().toString()+")");
                }
            }
        }
        //KITE ARENAS
        else if (key.equalsIgnoreCase("createkitearena")){
            if(args.length >= 2){
                String name = args[1];
                name = name.replaceAll("_"," ");

                Selection selection = Practice.getWorldEdit().getSelection(p);
                if(selection != null) {

                    KiteArena arena = new KiteArena(Practice.getPlugin(), Practice.getArenaManager().getNextArenaIndex(), name, p.getLocation(), p.getLocation(),
                            p.getLocation(), selection.getMinimumPoint(), selection.getMaximumPoint());
                    Practice.getArenaManager().registerArena(arena);
                    arena.save();
                    p.sendMessage(ChatColor.GREEN + "Created kite arena "+arena.getId()+" '" + name + "'.  Set it's spawns with /prac setkitearenaspawn.");
                }
                else{
                    p.sendMessage(ChatColor.RED+"Make a WorldEdit selection around the arena first.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("setkitearenaspawn")){
            if(args.length >= 3){
                try {
                    int id = Integer.parseInt(args[1]);

                    Arena a = Practice.getArenaManager().getArena(id);

                    if(a != null) {

                        if (a.getType() != ArenaType.KITE) {
                            p.sendMessage(ChatColor.RED + "That arena is not a kite arena.");
                            return;
                        }

                        KiteArena arena = (KiteArena) a;

                        if (args[2].equalsIgnoreCase("runner")) {
                            arena.setSpawnAlpha(p.getLocation());
                            arena.save();
                            p.sendMessage(ChatColor.GREEN + "Set spawn ALPHA for kite arena '" + arena.getName() + "'.");
                        } else if (args[2].equalsIgnoreCase("chaser")) {
                            arena.setSpawnBravo(p.getLocation());
                            arena.save();
                            p.sendMessage(ChatColor.GREEN + "Set spawn BRAVO for kite arena '" + arena.getName() + "'.");
                        } else if (args[2].equalsIgnoreCase("end")) {
                            arena.setEnd(p.getLocation());
                            arena.save();
                            p.sendMessage(ChatColor.GREEN + "Set END location for kite arena '" + arena.getName() + "'.");
                        } else {
                            p.sendMessage(ChatColor.RED + "Incorrect usage.");
                        }
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"Arena by ID "+id+" does not exist.");
                    }
                }
                catch (NumberFormatException expected){
                    p.sendMessage(ChatColor.RED+"The arena ID must be a number.");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else if (key.equalsIgnoreCase("dupearena")){//prac dupearena id offset offset
            if(args.length >= 4){
                try{
                    int id = Integer.parseInt(args[1]);
                    final int offsetX = Integer.parseInt(args[2]);
                    final int offsetZ = Integer.parseInt(args[3]);
                    final Arena arena = Practice.getArenaManager().getArena(id);
                    if(arena != null){
                        try {
                            ArenaDupeTask task = new ArenaDupeTask(arena, offsetX, offsetZ, 10, offsetX,offsetZ) {
                                @Override
                                public void onComplete(Arena dupe) {
                                    Practice.getArenaManager().registerArena(dupe);
                                    dupe.save();
                                    p.sendMessage(ChatColor.GREEN+"Arena dupe complete.  New arena ID: "+dupe.getId());
                                    p.teleport(dupe.getSpawnAlpha());
                                }
                            };
                            task.run();
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                            p.sendMessage(ChatColor.RED+"An error occurred.");
                        }
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"Arena by ID "+id+" does not exist.");
                    }
                }
                catch (NumberFormatException expected){
                    p.sendMessage(ChatColor.RED+"The arena ID/offsetX/offsetZ must be a number(integer).");
                }
            }
            else{
                p.sendMessage(ChatColor.RED+"Incorrect usage.");
            }
        }
        else{
            onCommand(new CmdArgs(p, new String[0]));
        }


    }
}
