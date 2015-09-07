package com.shawckz.ipractice.match.type;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.arena.ArenaManager;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.match.*;
import com.shawckz.ipractice.match.handler.BasicMatchHandler;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.Validate;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class BasicMatch extends Match implements Listener {

    @Getter private String id = UUID.randomUUID().toString().substring(0, 6);
    @Getter private Arena arena;
    @Getter private Ladder ladder;
    @Getter private long startedAt = -1;
    @Getter private int countdown = 5;
    private Map<String, MatchPlayer> players = new HashMap<>();
    private boolean started = false;
    private Map<String, MatchInventory> inventories = new HashMap<>();
    @Getter private boolean ranked = false;
    @Getter private final MatchHandler matchHandler;

    private final IPlayer alpha;
    private final IPlayer bravo;

    public BasicMatch(Ladder ladder, Player alpha, Player bravo, boolean ranked) {
        Validate.notNull(alpha);
        Validate.notNull(bravo);
        Validate.notNull(ladder);
        this.ladder = ladder;
        this.arena = Practice.getArenaManager().getNextArena();
        this.ranked = ranked;
        this.alpha = Practice.getCache().getIPlayer(alpha);
        this.bravo = Practice.getCache().getIPlayer(bravo);
        this.matchHandler = new BasicMatchHandler(Practice.getPlugin(), this);
        if(arena == null){
            throw new PracticeException("No arena could be found for (Basic)Match (none registered?)");
        }
        MatchPlayer alphaMP = new MatchPlayer(alpha, Team.ALPHA);
        players.put(alpha.getName(), alphaMP);
        MatchPlayer bravoMP = new MatchPlayer(bravo, Team.BRAVO);
        players.put(bravo.getName(), bravoMP);
    }

    @Override
    public Team getTeam(Player player) {
        if(players.containsKey(player.getName())){
            return players.get(player.getName()).getTeam();
        }
        return null;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public long getStarted() {
        return startedAt;
    }

    @Override
    public MatchInventory getInventory(Player player) {
        return inventories.get(player.getName());
    }

    @Override
    public void start() {
        msg(ChatColor.LIGHT_PURPLE + alpha.getName() + ChatColor.GOLD + " vs. " + ChatColor.LIGHT_PURPLE + bravo.getName());

        Practice.getMatchManager().registerMatch(this);
        matchHandler.register();
        for(MatchPlayer mp : players.values()){
            Player p = mp.getPlayer();
            //Equip kit & set state for all players in match
            IPlayer iPlayer = Practice.getCache().getIPlayer(p);
            iPlayer.equipKit(ladder);
            iPlayer.setState(PlayerState.IN_MATCH);

            //Visibility
            for(Player pl : Bukkit.getOnlinePlayers()){
                IPlayer ipl = Practice.getCache().getIPlayer(pl);
           //     if(!ipl.isStaffMode() && !players.containsKey(pl.getName())){
           //         //If they aren't in staff mode, hide the player for them
           //         Practice.getEntityHider().hideEntity(pl, p);
          //      }
                if(!players.containsKey(pl.getName())){
                    Practice.getEntityHider().hideEntity(p, pl);
                    if(Practice.getMatchManager().inMatch(ipl)){
                        Practice.getEntityHider().hideEntity(pl, p);
                    }
                }
                else{
                    Practice.getEntityHider().showEntity(p, pl);
                }
            }

            //Teleport
            if(getTeam(p) == Team.ALPHA){
                p.teleport(arena.getSpawnAlpha());
            }
            else if (getTeam(p) == Team.BRAVO){
                p.teleport(arena.getSpawnBravo());
            }
            else{
                throw new PracticeException("Invalid team '"+getTeam(p).toString()+"'");
            }
        }

        //Countdown
        new BukkitRunnable(){
            @Override
            public void run() {
                if(countdown > 0){
                    msg(ChatColor.YELLOW+"Starting in "+ChatColor.LIGHT_PURPLE+countdown
                            +ChatColor.YELLOW+"...");

                    countdown--;
                }
                else{
                    msg(ChatColor.GREEN+"Go!");
                    started = true;
                    startedAt = System.currentTimeMillis();
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getPlugin(), 20L, 20L);

    }

    @Override
    public void end() {
        matchHandler.unregister();
        HandlerList.unregisterAll(this);
        Practice.getMatchManager().unregisterMatch(this);
        for(MatchPlayer mp : players.values()){
            if(!mp.isEliminated()){
                Player p = mp.getPlayer();
                IPlayer ip = Practice.getCache().getIPlayer(p);
                ip.sendToSpawn();
                ip.setState(PlayerState.AT_SPAWN);
                for(Player pl : Bukkit.getOnlinePlayers()){
                    Practice.getEntityHider().showEntity(p, pl);
                    Practice.getEntityHider().showEntity(pl, p);
                }
            }
        }
    }

    @Override
    public Collection<MatchPlayer> getPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

    public void eliminate(Player p){
        if(players.containsKey(p.getName()) && !players.get(p.getName()).isEliminated()){
            MatchPlayer pmp = players.get(p.getName());
            pmp.setEliminated(true);
            inventories.put(p.getName(), new MatchInventory(p));
            if(p.getKiller() != null){
                msg(ChatColor.LIGHT_PURPLE+p.getName()+ChatColor.GOLD+" was killed by "+ChatColor.LIGHT_PURPLE+p.getKiller().getName());
            }
            else{
                msg(ChatColor.LIGHT_PURPLE+p.getName()+ChatColor.GOLD+" was killed.");
            }
            IPlayer ip = Practice.getCache().getIPlayer(p);
            ip.sendToSpawn();

            int alivePlayers = 0;
            for(MatchPlayer mp : players.values()){
                if(!mp.isEliminated()){
                    alivePlayers++;
                }
            }

            if(alivePlayers <= 1){
                handleWin(pmp.getTeam().getOpposite());
                //we don't have to worry about teams in this Match type.
            }
        }
    }

    private void handleWin(Team team){
        IPlayer winner;
        IPlayer loser;

        if(team == Team.ALPHA){
            winner = alpha;
            loser = bravo;
        }
        else{
            winner = bravo;
            loser = alpha;
        }

        if(ranked){
            int oldWElo = winner.getElo(ladder);
            int oldLElo = loser.getElo(ladder);

            winner.getWins().put(ladder, winner.getWins().get(ladder)+1);
            winner.updateElo(ladder, loser.getElo(ladder), true);

            loser.getLosses().put(ladder, loser.getLosses().get(ladder)+1);
            loser.updateElo(ladder, winner.getElo(ladder), false);

            int wChange = winner.getElo(ladder) - oldWElo;
            int lChange = loser.getElo(ladder) - oldLElo;

            msg(ChatColor.GOLD + "Elo Changes: "
                    + getEloChange(winner.getName(), wChange, winner.getElo(ladder))
                    + " " + getEloChange(loser.getName(), lChange, loser.getElo(ladder)));
        }

        inventories.put(winner.getName(), new MatchInventory(winner.getPlayer()));

        msg(ChatColor.GOLD + "Winner(s): " + ChatColor.LIGHT_PURPLE + winner.getName());

        sendInventories(winner.getPlayer());
        sendInventories(loser.getPlayer());

        new BukkitRunnable(){
            @Override
            public void run() {
                for(MatchPlayer mp : players.values()){
                    if(!mp.isEliminated()){
                        IPlayer ip = Practice.getCache().getIPlayer(mp.getPlayer());
                        ip.sendToSpawn();
                    }
                }
            }
        }.runTaskLater(Practice.getPlugin(),20L);
        end();
    }

    private void sendInventories(Player p) {
        FancyMessage fm = new FancyMessage(ChatColor.GOLD+"View Inventories: "+ChatColor.LIGHT_PURPLE);
        for (MatchPlayer mp : players.values()) {
            Player pl = mp.getPlayer();
            if (inventories.containsKey(pl.getName())) {
                fm.then(pl.getName() + " ").tooltip(ChatColor.GOLD+"View "+ ChatColor.AQUA + pl.getName() +
                        ChatColor.GOLD+"'s Inventory").command("/viewinv "
                        + inventories.get(pl.getName()).getUuid());
            }
        }
        fm.send(p);
    }

    private String getEloChange(String name, int change, int elo){
        return ChatColor.AQUA+name+""+ChatColor.DARK_GRAY+"["+ChatColor.LIGHT_PURPLE+elo
                +ChatColor.YELLOW+"("
                +(change >= 0 ? ChatColor.GREEN+"+"+change : ChatColor.RED+""+change)
                +ChatColor.YELLOW+")"
                + ChatColor.DARK_GRAY+"]";
    }

    public void msg(String msg){
        for(MatchPlayer p : getPlayers()){
            p.getPlayer().sendMessage(msg);
        }
    }

    @Override
    public boolean hasPlayer(Player player) {
        return players.containsKey(player.getName());
    }





}
