package com.shawckz.ipractice.match.handle;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.MatchBuilder;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.player.IPlayer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by 360 on 9/7/2015.
 */
public class MatchManager {

    public MatchManager(Practice instance) {
    }

    public MatchBuilder matchBuilder(Ladder ladder){
        return new MatchBuilder(ladder);
    }

    private final Map<String, PracticeMatch> matches = new HashMap<>();//Id, Match

    public PracticeMatch getMatch(String id){
        return matches.get(id);
    }

    public boolean inMatch(IPlayer player){
        PracticeMatch match = getMatch(player);
        if(match != null){
            return true;
        }
        return false;
    }

    public PracticeMatch getMatch(IPlayer player){
        for(PracticeMatch match : matches.values()){
            if(player != null && match != null && match.getPlayers().contains(player.getPlayer())
                    && !match.isOver()){
                return match;
            }
        }
        return null;
    }

    public void registerMatch(PracticeMatch match){
        matches.put(match.getId(), match);
    }

    public void unregisterMatch(Match match){
        if(matches.containsKey(match.getId())){
            matches.remove(match.getId());
        }
    }

    public int getAmountOfPlayersInMatches(Ladder ladder){
        int i = 0;
        for(Player pl : Bukkit.getOnlinePlayers()){
            IPlayer ip = Practice.getCache().getIPlayer(pl);
            if(inMatch(ip)){
                if(getMatch(ip).getLadder().getName().equals(ladder.getName())){
                    i++;
                }
            }
        }
        return i;
    }

}
