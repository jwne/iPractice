package com.shawckz.ipractice.match;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.arena.ArenaType;
import com.shawckz.ipractice.arena.BasicArena;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.handle.MatchHandler;
import com.shawckz.ipractice.match.handle.MatchManager;
import com.shawckz.ipractice.match.participant.MatchParticipant;
import com.shawckz.ipractice.match.participant.MatchPlayer;
import com.shawckz.ipractice.match.participant.MatchPlayerManager;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
import com.shawckz.ipractice.match.team.TeamManager;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.practice.state.MatchBoardType;
import com.shawckz.ipractice.scoreboard.practice.state.PracticeBoardType;
import com.shawckz.ipractice.util.nametag.Nametag;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by 360 on 9/7/2015.
 */
@Getter
public class Match implements PracticeMatch {

    public static final Nametag FRIENDLY_FIRE = new Nametag("noff", "", "", false, false);

    @Setter private BasicArena arena = null;
    private final String id;
    private boolean started = false;
    @Setter private boolean ranked = false;
    private final Ladder ladder;
    private int countdown = 5;
    private final TeamManager teamManager;
    private final MatchPlayerManager playerManager;
    private MatchManager matchManager;
    private boolean over = false;
    private final MatchHandler matchHandler;
    private final Map<String, String> inventories = new HashMap<>();

    public Match(Ladder ladder) {
        this.id = UUID.randomUUID().toString();
        this.ladder = ladder;
        this.teamManager = new TeamManager(this);
        this.playerManager = new MatchPlayerManager(this);
        this.matchHandler = new MatchHandler(this);
    }

    public void startMatch(MatchManager matchManager){
        this.matchManager = matchManager;
        teamManager.checkPerquisites();
        if(arena == null){
            arena = (BasicArena) Practice.getArenaManager().getNextArena(ArenaType.NORMAL);
        }
        matchManager.registerMatch(this);
        matchHandler.register();

        String versus = "";
        for(PracticeTeam team : teamManager.getTeams().values()){
            versus += ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.GOLD+" vs. ";
        }
        versus = versus.substring(0, versus.length() - 5);
        msg(versus);

        this.over = false;


        countdown = 5;
        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pmmp : pmp.getPlayers()){
                IPlayer ip = pmmp.getPlayer();
                ip.equipKit(ladder);
                ip.setState(PlayerState.IN_MATCH);
                ip.getScoreboard().update();
            }
        }

        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pmmp : pmp.getPlayers()){
                IPlayer ip = pmmp.getPlayer();
                if(pmp.getTeam().getSpawn() == Team.ALPHA){
                    ip.getPlayer().teleport(arena.getSpawnAlpha());
                }
                else if(pmp.getTeam().getSpawn() == Team.BRAVO){
                    ip.getPlayer().teleport(arena.getSpawnBravo());
                }
                else{
                    throw new PracticeException("Unknown Team enumeration: "+pmp.getTeam().getSpawn().toString());
                }
                ip.handlePlayerVisibility();
            }
        }



        new BukkitRunnable(){
            @Override
            public void run() {
                if(over) {
                    cancel();
                    return;
                }
                if(countdown > 0){
                    msg(ChatColor.GOLD+"Starting in "+ChatColor.LIGHT_PURPLE+countdown+ChatColor.GOLD+"...");
                    countdown--;
                }
                else{
                    countdown = 0;
                    started = true;
                    over = false;
                    msg(ChatColor.GREEN+"Go!");
                    for(MatchParticipant pmp : playerManager.getParticipants()){
                        for(MatchPlayer pmmp : pmp.getPlayers()){
                            IPlayer ip = pmmp.getPlayer();
                            ip.getScoreboard().update();
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getPlugin(), 20L, 20L);

    }

    public void endMatch(){
        this.over = true;
        new BukkitRunnable(){
            @Override
            public void run() {
                for(MatchParticipant pl : playerManager.getParticipants()){
                    for(MatchPlayer p : pl.getPlayers()){
                        if(p.isAlive()){
                            if(ranked){
                                p.getPlayer().incrementWins(ladder);
                            }
                            p.getPlayer().sendToSpawn();
                        }
                        else{
                            if(ranked){
                                p.getPlayer().incrementLosses(ladder);
                            }
                            if(!started){
                                p.getPlayer().sendToSpawn();
                            }
                        }
                    }
                }
            }
        }.runTaskLater(Practice.getPlugin(), 100L);

        matchHandler.unregister();
        matchManager.unregisterMatch(this);
    }

    public void eliminatePlayer(IPlayer player, IPlayer killer){
        MatchParticipant participant = playerManager.getParticipant(player);
        if(participant != null){
            playerManager.getPlayer(player).setAlive(false);

            if(killer != null){
                msg(ChatColor.BLUE+player.getName()+ChatColor.GOLD+" was killed by "+ChatColor.BLUE+killer.getName()+ChatColor.GOLD+".");
            }
            else{
                msg(ChatColor.BLUE+player.getName()+ChatColor.GOLD+" was killed.");
            }

            inventories.put(player.getName(), new MatchInventory(player.getPlayer()).getUuid());

            if(ranked){
                player.getDeaths().put(ladder, (player.getDeaths().get(ladder)+1));
                if(killer != null){
                    killer.getKills().put(ladder, (player.getKills().get(ladder)+1));
                }
            }


            boolean shouldEliminate = true;
            PracticeTeam team = participant.getTeam();

            for(MatchParticipant pl : playerManager.getParticipants()){
                if(pl.getTeam().getName().equals(team.getName())){
                    for(MatchPlayer pla : pl.getPlayers()){
                        if(pla.isAlive()){
                            shouldEliminate = false;
                            break;
                        }
                    }
                }
            }
            if(shouldEliminate){
                team.setEliminated(true);
            }

            int remainingTeams = 0;
            for(PracticeTeam t : teamManager.getTeams().values()){
                if(!t.isEliminated()){
                    remainingTeams++;
                }
            }

            if(remainingTeams <= 1){
                //Only one team is left, that team wins
                for(PracticeTeam t : teamManager.getTeams().values()){
                    if(!t.isEliminated()){
                        handleWin(t);
                        break;
                    }
                }
            }
            else{
                //there are still more players left, send the dead player to spawn
                player.sendToSpawn();
            }

        }
        else{
            throw new PracticeException("Can not eliminate null player");
        }
    }

    public void handleWin(final PracticeTeam team){
        msg(ChatColor.GOLD + "Winner(s): " + ChatColor.LIGHT_PURPLE + team.getName());

        for(MatchParticipant pl : playerManager.getParticipants()){
            if(pl.getTeam().getName().equals(team.getName())){
                for(MatchPlayer mp : pl.getPlayers()){
                    if(mp.isAlive()) {
                        inventories.put(mp.getPlayer().getName(), new MatchInventory(mp.getPlayer().getPlayer()).getUuid());
                    }
                }
            }
        }

        sendInventories();

        if(ranked){
            int winnerElo = 0;
            int wx = 0;
            int loserElo = 0;
            int lx = 0;

            Map<String, Integer> ogElo = new HashMap<>();
            Map<String, Integer> newElo = new HashMap<>();

            for(MatchParticipant pl : playerManager.getParticipants()){
                for(MatchPlayer p : pl.getPlayers()){
                    if(teamManager.getTeam(p.getPlayer()).getName().equals(team.getName())){
                        winnerElo += p.getPlayer().getElo(ladder);
                        wx++;
                    }
                    else{
                        loserElo += p.getPlayer().getElo(ladder);
                        lx++;
                    }
                    ogElo.put(p.getPlayer().getName(), p.getPlayer().getElo(ladder));
                }
            }

            winnerElo /= wx;
            loserElo /= lx;

            for(MatchParticipant pl : playerManager.getParticipants()){
                for(MatchPlayer p : pl.getPlayers()){
                    if(teamManager.getTeam(p.getPlayer()).getName().equals(team.getName())){
                        p.getPlayer().updateElo(ladder, loserElo, true);
                    }
                    else{
                        p.getPlayer().updateElo(ladder, winnerElo, false);
                    }
                    newElo.put(p.getPlayer().getName(), p.getPlayer().getElo(ladder));
                }
            }

            sendEloChanges(ogElo, newElo);
        }

        endMatch();
    }

    private void sendEloChanges(Map<String,Integer> before, Map<String,Integer> after){
        String s = "";

        for(String k : before.keySet()){
            int difference = after.get(k) - before.get(k);
            int elo = after.get(k);
            s += ChatColor.LIGHT_PURPLE+k+ChatColor.DARK_GRAY+"["+ChatColor.BLUE+elo+
                    ChatColor.YELLOW+"("+(difference >= 0 ? ChatColor.GREEN+"+"+
                    difference : ChatColor.RED+""+difference)
                    +ChatColor.YELLOW+")"+ChatColor.DARK_GRAY+"] ";
            //Player1[1010(+10)]
            //Player2[990(-10)]
        }

        msg(ChatColor.GOLD + "Elo Changes: " + s);
    }

    private void sendInventories(){
        FancyMessage fm = new FancyMessage(ChatColor.GOLD+"Inventories(Click to view): ");
        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pl : pmp.getPlayers()){
                if(inventories.containsKey(pl.getPlayer().getName())){
                    fm.then(pl.getPlayer().getName()).tooltip(ChatColor.BLUE+pl.getPlayer().getName())
                    .tooltip(ChatColor.GREEN+"View "+pl.getPlayer().getName()+"'s Inventory")
                    .command("/viewinv "+inventories.get(pl.getPlayer().getName()));
                    fm.then(" ");
                }
            }
        }
        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pl : pmp.getPlayers()){
                if(inventories.containsKey(pl.getPlayer().getName())){
                    fm.send(pl.getPlayer().getPlayer());
                }
            }
        }
    }

    public void msg(String msg){
        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pl : pmp.getPlayers()){
                pl.getPlayer().getPlayer().sendMessage(msg);
            }
        }
    }

    @Override
    public Set<Player> getPlayers() {
        return playerManager.getAllPlayers();
    }

    @Override
    public MatchType getType() {
        return MatchType.NORMAL;
    }
}
