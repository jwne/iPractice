package com.shawckz.ipractice;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.shawckz.ipractice.arena.ArenaManager;
import com.shawckz.ipractice.command.CommandHandler;
import com.shawckz.ipractice.configuration.IConfig;
import com.shawckz.ipractice.database.DBManager;
import com.shawckz.ipractice.event.EventManager;
import com.shawckz.ipractice.listener.ChatListener;
import com.shawckz.ipractice.listener.KitInvClose;
import com.shawckz.ipractice.listener.WorldListener;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.match.MatchManager;
import com.shawckz.ipractice.party.PartiesInv;
import com.shawckz.ipractice.party.PartyManager;
import com.shawckz.ipractice.player.ICache;
import com.shawckz.ipractice.queue.QueueManager;
import com.shawckz.ipractice.queue.QueueMatchSet;
import com.shawckz.ipractice.spawn.Spawn;
import com.shawckz.ipractice.util.EntityHider;
import lombok.Getter;

import org.bukkit.Bukkit;
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

        Bukkit.getPluginManager().registerEvents(new KitInvClose(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(),this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(),this);
        Bukkit.getPluginManager().registerEvents(new PartiesInv(),this);


    }

    @Override
    public void onDisable(){
        dbManager.shutdown();
        iConfig.save();
        cache.clearCache();

        cache = null;
        dbManager = null;
        iConfig = null;
        plugin = null;
    }

}
