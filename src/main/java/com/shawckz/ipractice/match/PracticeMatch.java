package com.shawckz.ipractice.match;

import java.util.Set;

import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.handle.MatchManager;
import com.shawckz.ipractice.player.IPlayer;

import org.bukkit.entity.Player;

public interface PracticeMatch {

    String getId();

    void startMatch(MatchManager matchManager);

    void endMatch();

    boolean isStarted();

    boolean isOver();

    Set<Player> getPlayers();

    MatchType getType();

    Ladder getLadder();

    String getOpponent(IPlayer player);

}
