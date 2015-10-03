package com.shawckz.ipractice.match.participant;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.participant.MatchParticipant;
import com.shawckz.ipractice.match.participant.MatchPlayer;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 9/8/2015.
 */
@RequiredArgsConstructor
public class MatchPlayerManager {

    private final Match match;
    @Getter private final Set<MatchParticipant> participants = new HashSet<>();

    public void addParticipant(Party player, PracticeTeam team){
        for(Player pl : player.getAllPlayers()){
            IPlayer ip = Practice.getCache().getIPlayer(pl);
            addParticipant(ip, team);
        }
    }

    public void addParticipant(IPlayer player, PracticeTeam team){
        MatchParticipant pmp = new MatchParticipant(player, team);
        if(!match.getTeamManager().hasTeam(team.getName())){
            throw new PracticeException("Attempted to add '"+player.getName()+"' to unregistered team '"+team.getName()+"'");
        }
        addParticipant(pmp);
    }

    public void addParticipant(MatchParticipant player){
        participants.add(player);
    }

    public boolean hasPlayer(Player player){
        IPlayer ip = Practice.getCache().getIPlayer(player);
        return hasPlayer(ip);
    }

    public boolean hasPlayer(IPlayer player){
        for(MatchParticipant pmp : participants){
            for(MatchPlayer pl : pmp.getPlayers()){
                if(pl.getPlayer().getName().equals(player.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    public MatchParticipant getParticipant(Player player){
        return getParticipant(Practice.getCache().getIPlayer(player));
    }

    public MatchParticipant getParticipant(IPlayer player){
        for(MatchParticipant pmp : participants){
            for(MatchPlayer mp : pmp.getPlayers()){
                if(mp.getPlayer().getName().equals(player.getName())){
                    return pmp;
                }
            }
        }
        return null;
    }

    public MatchPlayer getPlayer(IPlayer player){
        MatchParticipant participant = getParticipant(player);
        if(participant != null){
            for(MatchPlayer pl : participant.getPlayers()){
                if(pl.getPlayer().getName().equals(player.getName())){
                    return pl;
                }
            }
        }
        return null;
    }

    public Set<Player> getAllPlayers(){
        Set<Player> pl = new HashSet<>();
        for(MatchParticipant pmp : participants){
            for(MatchPlayer pll : pmp.getPlayers()){
                pl.add(pll.getPlayer().getPlayer());
            }
        }
        return pl;
    }

    public Set<MatchPlayer> getMatchPlayers(){
        Set<MatchPlayer> pl = new HashSet<>();
        for(MatchParticipant pmp : participants){
            for(MatchPlayer pll : pmp.getPlayers()){
                pl.add(pll);
            }
        }
        return pl;
    }

}
