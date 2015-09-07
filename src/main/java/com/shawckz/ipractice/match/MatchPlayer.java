package com.shawckz.ipractice.match;

import lombok.Data;

import org.bukkit.entity.Player;

@Data
public class MatchPlayer {

    private final Player player;
    private final Team team;
    private boolean eliminated = false;

}
