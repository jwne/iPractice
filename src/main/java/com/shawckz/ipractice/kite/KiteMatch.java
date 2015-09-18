package com.shawckz.ipractice.kite;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.ArenaType;
import com.shawckz.ipractice.arena.KiteArena;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.handle.MatchManager;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
@Getter
public class KiteMatch implements PracticeMatch{

    public static final String KITE_LADDER_RUNNER = "KiteRunner";
    public static final String KITE_LADDER_CHASER = "KiteChaser";

    private final String id = UUID.randomUUID().toString();
    private final IPlayer runner;
    private final IPlayer chaser;
    private boolean started = false;
    private boolean over = false;
    private int runnerCountdown = 5;
    private int chaserCountdown = 8;
    private KiteMatchHandler matchHandler;
    @Setter private KiteArena arena;

    @Override
    public void startMatch(MatchManager matchManager){
        if(started){
            throw new PracticeException("Attempted to start KiteMatch when already running");
        }

        if(arena == null){
            arena = (KiteArena) Practice.getArenaManager().getNextArena(ArenaType.KITE);
        }
        else{
            if(arena.getType() != ArenaType.KITE){
                throw new PracticeException("KiteMatch: Arena is not Kite Arena");
            }
        }

        if(arena == null){
            throw new PracticeException("KiteMatch: There are no kite arenas registered");
        }

        runner.getPlayer().teleport(arena.getSpawnAlpha());
        chaser.getPlayer().teleport(arena.getSpawnBravo());

        runner.setState(PlayerState.IN_MATCH);
        chaser.setState(PlayerState.IN_MATCH);

        runner.equipKit(Ladder.getLadder(KITE_LADDER_RUNNER));
        chaser.equipKit(Ladder.getLadder(KITE_LADDER_CHASER));

        matchHandler = new KiteMatchHandler(this);
        matchHandler.register();
        new BukkitRunnable(){
            @Override
            public void run() {
                if(runnerCountdown > 0){
                    runnerCountdown--;
                    runner.getPlayer().sendMessage(ChatColor.GOLD + "Start Kiting in " +
                            ChatColor.LIGHT_PURPLE + runnerCountdown + ChatColor.GOLD + "...");
                }
                else{
                    runner.getPlayer().sendMessage(ChatColor.GREEN + "Go!");
                }

                if(chaserCountdown > 0){
                    chaserCountdown--;
                    chaser.getPlayer().sendMessage(ChatColor.GOLD + "You can start chasing in " +
                            ChatColor.LIGHT_PURPLE + chaserCountdown + ChatColor.GOLD + "...");
                    if(runnerCountdown <= 0){
                        runner.getPlayer().sendMessage(ChatColor.BLUE+chaser.getName()+ChatColor.GOLD+
                                " will start chasing you in "
                                +ChatColor.LIGHT_PURPLE+chaserCountdown+ChatColor.GOLD+"...");
                    }
                }
                else{
                    chaser.getPlayer().sendMessage(ChatColor.GREEN + "Go!");
                    started = true;
                }

            }
        }.runTaskTimer(Practice.getPlugin(),20L,20L);
    }

    public void msg(String msg){
        runner.getPlayer().sendMessage(msg);
        chaser.getPlayer().sendMessage(msg);
    }

    @Override
    public void endMatch() {
        if(!started || over){
            throw new PracticeException("Attempted to end KiteMatch when already over or not started (started:"+started+",over:"+over+")");
        }
        matchHandler.unregister();
        started = false;
        over = true;

        if(runner != null){
            runner.sendToSpawn();
        }
        if(chaser != null){
            chaser.sendToSpawn();
        }
    }

    public void eliminatePlayer(Player player){
        if(runner.getName().equals(player.getName())){
            msg(ChatColor.LIGHT_PURPLE+chaser.getName()+ChatColor.GOLD+" has won!");
            endMatch();
        }
        else if (chaser.getName().equals(player.getName())){
            msg(ChatColor.LIGHT_PURPLE+runner.getName()+ChatColor.GOLD+" has won!");
            endMatch();
        }
        else{
            throw new PracticeException("Not in KiteMatch?: "+player.getName());
        }
    }

    public boolean contains(Player player){
        return chaser.getPlayer().getName().equals(player.getName()) ||
                runner.getPlayer().getName().equals(player.getName());
    }

    public KiteRole getRole(Player player){
        if(contains(player)){
            if(chaser.getPlayer().getName().equals(player.getName())){
                return KiteRole.CHASER;
            }
            else{
                return KiteRole.RUNNER;
            }
        }
        return null;
    }

    @Override
    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        players.add(runner.getPlayer());
        players.add(chaser.getPlayer());
        return players;
    }
}
