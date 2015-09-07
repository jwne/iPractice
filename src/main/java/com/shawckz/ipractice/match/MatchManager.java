package com.shawckz.ipractice.match;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;

import java.util.HashMap;
import java.util.Map;

public class MatchManager {

    public MatchManager(Practice instance) {}

    private final Map<String, Match> matches = new HashMap<>();

    public Match getMatch(String id){
        return matches.get(id);
    }

    public void registerMatch(Match match){
        matches.put(match.getId(), match);
    }

    public void unregisterMatch(Match match){
        if(matches.containsKey(match.getId())){
            matches.remove(match.getId());
        }
    }

    public Match getMatch(IPlayer player){
        for(Match match : matches.values()){
            for(MatchPlayer mp : match.getPlayers()){
                if(mp.getPlayer().getName().equals(player.getName())){
                    return match;
                }
            }
        }
        return null;
    }

    public boolean inMatch(IPlayer player){
        for(Match match : matches.values()){
            for(MatchPlayer mp : match.getPlayers()){
                if(mp.getPlayer().getName().equals(player.getName())){
                    return true;
                }
            }
        }
        return false;
    }

}
