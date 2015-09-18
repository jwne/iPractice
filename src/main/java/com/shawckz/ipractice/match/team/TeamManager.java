package com.shawckz.ipractice.match.team;

import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.participant.MatchParticipant;
import com.shawckz.ipractice.match.participant.MatchPlayer;
import com.shawckz.ipractice.player.IPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 360 on 9/8/2015.
 */
@RequiredArgsConstructor
public class TeamManager {

    private final Match match;
    @Getter private final Map<String, PracticeTeam> teams = new HashMap<>();

    public void checkPerquisites(){
        if(teams.isEmpty() || match.getPlayerManager().getParticipants().isEmpty()){
            throw new PracticeException("Could not start match: teams or participants are empty");
        }
        if(match.isStarted()){
            throw new PracticeException("Could not start match: Match has already started");
        }
        if(teams.size() < 2){
            throw new PracticeException("Could not start match: There must be at least 2 teams registered");
        }
    }

    public void registerTeam(PracticeTeam team){
        teams.put(team.getName(), team);
    }

    public PracticeTeam getTeam(String teamName){
        return teams.get(teamName);
    }

    public boolean hasTeam(String teamName){
        return teams.containsKey(teamName);
    }

    public PracticeTeam getTeam(IPlayer player){
        for(MatchParticipant pmp : match.getPlayerManager().getParticipants()){
            for(MatchPlayer pl : pmp.getPlayers()){
                if(pl.getPlayer().getName().equals(player.getName())){
                    return pmp.getTeam();
                }
            }
        }
        return null;
    }

}
