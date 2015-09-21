package com.shawckz.ipractice;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.shawckz.ipractice.arena.ArenaManager;
import com.shawckz.ipractice.command.CommandHandler;
import com.shawckz.ipractice.configuration.IConfig;
import com.shawckz.ipractice.database.DBManager;
import com.shawckz.ipractice.event.EventManager;
import com.shawckz.ipractice.listener.*;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.handle.MatchManager;
import com.shawckz.ipractice.party.PartiesInv;
import com.shawckz.ipractice.party.PartyManager;
import com.shawckz.ipractice.player.ICache;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.QueueManager;
import com.shawckz.ipractice.spawn.Spawn;
import com.shawckz.ipractice.task.TaskAutoSave;
import com.shawckz.ipractice.task.TaskClearEntities;
import com.shawckz.ipractice.util.EntityHider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

    @Getter private static Plugin plugin;
    @Getter private static ICache cache;
    @Getter private static DBManager dbManager;
    @Getter private static IConfig iConfig;
    @Getter private static ArenaManager arenaManager;
    @Getter private static CommandHandler commandHandler;
    @Getter private static Spawn spawn;
    @Getter private static PartyManager partyManager;
    @Getter private static MatchManager matchManager;
    @Getter private static ProtocolManager protocolManager;
    @Getter private static EntityHider entityHider;
    @Getter private static EventManager eventManager;
    @Getter private static QueueManager queueManager;
    @Getter private static TaskAutoSave taskAutoSave;

    @Override
    public void onEnable(){
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        dbManager = new DBManager(this);
        cache = new ICache(this);
        iConfig = new IConfig(this);//load & save called within the constructor
        commandHandler = new CommandHandler(this);
        spawn = new Spawn(this);
        partyManager = new PartyManager(this);
        matchManager = new MatchManager(this);
        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        arenaManager = new ArenaManager(this);
        eventManager = new EventManager(this);
        queueManager = new QueueManager(this);
        queueManager.run();
        Ladder.loadLadders(this);

        getServer().getPluginManager().registerEvents(new KitInvClose(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(),this);
        getServer().getPluginManager().registerEvents(new PartiesInv(),this);
        getServer().getPluginManager().registerEvents(new StaffModeListener(), this);
        getServer().getPluginManager().registerEvents(new Soup(), this);
        taskAutoSave = new TaskAutoSave();
        getServer().getScheduler().runTaskTimerAsynchronously(this, taskAutoSave, 15000, 15000);
        getServer().getScheduler().runTaskTimer(this, new TaskClearEntities(), 600, 600);
    }

    @Override
    public void onDisable(){
        taskAutoSave.run();
        if(!eventManager.canStartEvent()){
            eventManager.endEvent();
        }
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(queueManager.inQueue(Practice.getCache().getIPlayer(pl))){
                queueManager.removeFromQueue(Practice.getCache().getIPlayer(pl));
            }
            //IPlayer ip = getCache().getIPlayer(pl);
            //ip.update();
            // -- Handled by TaskAutoSave ^^
        }

        cache.clearCache();
        dbManager.shutdown();
        iConfig.save();
        cache = null;
        dbManager = null;
        commandHandler.getCommands().clear();
        commandHandler = null;
        spawn = null;
        partyManager.getParties().clear();
        partyManager = null;
        iConfig = null;
        matchManager = null;
        entityHider = null;
        arenaManager.getArenas().clear();;
        arenaManager = null;
        eventManager = null;

        plugin = null;
    }

}
