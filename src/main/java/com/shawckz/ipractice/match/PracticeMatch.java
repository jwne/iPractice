package com.shawckz.ipractice.match;

import java.util.Set;

import org.bukkit.entity.Player;

public interface PracticeMatch {

    void startMatch();

    void endMatch();

    boolean isStarted();

    boolean isOver();

    Set<Player> getPlayers();

}
