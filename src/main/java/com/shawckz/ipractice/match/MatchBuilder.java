package com.shawckz.ipractice.match;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.arena.BasicArena;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
import com.shawckz.ipractice.party.Party;
import org.bukkit.entity.Player;

/**
 * Created by 360 on 9/8/2015.
 */
public class MatchBuilder {

    private final Match match;

    public MatchBuilder(Ladder ladder) {
        this.match = new Match(ladder);
    }

    public MatchBuilder createTeam(String name, Team spawn){
        if(match.getTeamManager().hasTeam(name)){
            throw new PracticeException("MatchBuider: There is already a team named '"+name+"'");
        }
        match.getTeamManager().registerTeam(new PracticeTeam(name, spawn));
        return this;
    }

    public MatchBuilder registerTeam(PracticeTeam team){
        if(match.getTeamManager().hasTeam(team.getName())){
            throw new PracticeException("MatchBuider: There is already a team named '"+team.getName()+"'");
        }
        match.getTeamManager().registerTeam(team);
        return this;
    }

    public MatchBuilder withPlayer(Player player, PracticeTeam team){
        return withPlayer(player, team.getName());
    }

    public MatchBuilder withPlayer(Player player, String team){
        if(match.getPlayerManager().hasPlayer(player)){
            throw new PracticeException("MatchBuilder: Match already has a player '"+player.getName()+"'");
        }
        PracticeTeam t = match.getTeamManager().getTeam(team);
        if(t != null){
            match.getPlayerManager().addParticipant(Practice.getCache().getIPlayer(player), t);
        }
        else{
            throw new PracticeException("MatchBuilder: Could not add player to match builder, team '"+team+"' does not exist.");
        }
        return this;
    }

    public MatchBuilder withParty(Party party, String team){
        PracticeTeam t = match.getTeamManager().getTeam(team);
        if(t != null){
            for(Player pl : party.getAllPlayers()){
                withPlayer(pl, t);
            }
        }
        else{
            throw new PracticeException("MatchBuilder: Could not add player to match builder, team '"+team+"' does not exist.");
        }
        return this;
    }

    public MatchBuilder setArena(BasicArena arena){
        match.setArena(arena);
        return this;
    }

    public MatchBuilder setRanked(boolean ranked){
        match.setRanked(ranked);
        return this;
    }

    public Match build(){
        return match;
    }

}
