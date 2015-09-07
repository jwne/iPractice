package com.shawckz.ipractice.match;

import com.shawckz.ipractice.arena.Arena;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

public abstract class Match {

    public abstract String getId();

    public abstract Team getTeam(Player player);

    public abstract boolean isStarted();

    public abstract Arena getArena();

    public abstract Ladder getLadder();

    public abstract long getStarted();

    public abstract MatchInventory getInventory(Player player);

    public abstract void start();

    public abstract void end();

    public abstract Collection<MatchPlayer> getPlayers();

    public abstract boolean isRanked();

    public abstract boolean hasPlayer(Player player);

    public abstract MatchHandler getMatchHandler();

    public abstract void eliminate(Player player);

}
