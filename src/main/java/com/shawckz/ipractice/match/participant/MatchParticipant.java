package com.shawckz.ipractice.match.participant;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 9/7/2015.
 */
@Getter
public class MatchParticipant {

    private final Set<MatchPlayer> players = new HashSet<>();
    private MatchPlayer singlePlayer = null;
    private boolean single = false;
    private final PracticeTeam team;

    public MatchParticipant(IPlayer iPlayer, PracticeTeam team) {
        MatchPlayer pmp = new MatchPlayer(iPlayer);
        this.players.add(pmp);
        this.singlePlayer = pmp;
        this.team = team;
        this.single = true;
    }

    public MatchParticipant(Party party, PracticeTeam team) {
        this.team = team;
        for(Player pl : party.getAllPlayers()){
            IPlayer ip = Practice.getCache().getIPlayer(pl);
            players.add(new MatchPlayer(ip));
        }
        this.single = false;
    }

    public int getAverageElo(Ladder ladder){
        int elo = 0;
        int x = 0;
        for(MatchPlayer pl : players){
            elo += pl.getPlayer().getElo(ladder);
            x++;
        }
        return Math.round(elo / x);
    }

}
