package com.shawckz.ipractice.match;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.match.Team;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by 360 on 9/7/2015.
 */
@Getter
public class Match {

    @Setter private Arena arena = null;
    private final String id;
    private boolean started = false;
    @Setter private boolean ranked = false;
    private final Ladder ladder;
    private int countdown = 5;
    @Getter private final TeamManager teamManager;
    @Getter private final MatchPlayerManager playerManager;
    @Getter private MatchManager matchManager;
    @Getter private boolean over = false;

    public Match(Ladder ladder) {
        this.id = UUID.randomUUID().toString();
        this.ladder = ladder;
        this.teamManager = new TeamManager(this);
        this.playerManager = new MatchPlayerManager(this);
    }

    public void startMatch(MatchManager matchManager){
        this.matchManager = matchManager;
        teamManager.checkPerquisites();
        if(arena == null){
            arena = Practice.getArenaManager().getNextArena();
        }
        matchManager.registerMatch(this);

        String versus = "";
        for(PracticeTeam team : teamManager.getTeams().values()){
            versus += ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.GOLD+" vs. ";
        }
        versus = versus.substring(0, versus.length() - 5);
        msg(versus);

        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pmmp : pmp.getPlayers()){
                IPlayer ip = pmmp.getPlayer();
                ip.equipKit(ladder);
                ip.setState(PlayerState.IN_MATCH);
                ip.handlePlayerVisibility();
                if(pmp.getTeam().getSpawn() == Team.ALPHA){
                    ip.getPlayer().teleport(arena.getSpawnAlpha());
                }
                else if(pmp.getTeam().getSpawn() == Team.BRAVO){
                    ip.getPlayer().teleport(arena.getSpawnBravo());
                }
                else{
                    throw new PracticeException("Unknown Team enumeration: "+pmp.getTeam().getSpawn().toString());
                }
            }
        }

        countdown = 5;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(countdown > 0){
                    msg(ChatColor.GOLD+"Starting in "+ChatColor.LIGHT_PURPLE+countdown+ChatColor.GOLD+"...");
                    countdown--;
                }
                else{
                    started = true;
                    over = false;
                    msg(ChatColor.GREEN+"Go!");
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getPlugin(), 20L, 20L);

    }

    public void endMatch(){
        if(!started){
            throw new PracticeException("Could not end match: Match has not started");
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for(MatchParticipant pl : playerManager.getParticipants()){
                    for(MatchPlayer p : pl.getPlayers()){
                        if(p.isAlive()){
                            p.getPlayer().sendToSpawn();
                        }
                    }
                }
            }
        }.runTaskLater(Practice.getPlugin(), 100L);

        //TODO: Unregister match handler
        matchManager.unregisterMatch(this);
        this.over = true;

        this.started = false;
    }

    public void eliminatePlayer(IPlayer player){
        MatchParticipant participant = playerManager.getParticipant(player);
        if(participant != null){
            if(!playerManager.getPlayer(player).isAlive()){
                throw new PracticeException("Tried to eliminate player that is already eliminated: "+player.getName());
            }
            playerManager.getPlayer(player).setAlive(false);
            boolean shouldEliminate = false;
            PracticeTeam team = participant.getTeam();

            for(MatchParticipant pl : playerManager.getParticipants()){
                if(pl.getTeam().getName().equals(team.getName())){
                    for(MatchPlayer pla : pl.getPlayers()){
                        if(pla.isAlive()){
                            shouldEliminate = true;
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
                player.sendToSpawn();
            }

        }
        else{
            throw new PracticeException("Can not eliminated null player");
        }
    }

    public void handleWin(final PracticeTeam team){
        msg(ChatColor.GOLD+"Winner(s): "+ChatColor.LIGHT_PURPLE+team.getName());

        endMatch();
    }

    public void msg(String msg){
        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pl : pmp.getPlayers()){
                pl.getPlayer().getPlayer().sendMessage(msg);
            }
        }
    }

}
