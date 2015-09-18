package com.shawckz.ipractice.queue.member;

import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.range.QueueRange;
import com.shawckz.ipractice.queue.range.UnrankedQueueRange;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
@RequiredArgsConstructor
public class UnrankedQueueMember implements QueueMember {

    private final IPlayer player;
    private final Ladder ladder;
    private final UnrankedQueueRange range = new UnrankedQueueRange();

    @Override
    public Set<IPlayer> getPlayers() {
        Set<IPlayer> players = new HashSet<>();
        players.add(player);
        return players;
    }

    @Override
    public Ladder getLadder() {
        return ladder;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public QueueRange getRange() {
        return range;
    }
}
