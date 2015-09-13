package com.shawckz.ipractice.queue.member;

import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.range.EloRange;
import com.shawckz.ipractice.queue.range.QueueRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
@RequiredArgsConstructor
public class RankedQueueMember implements QueueMember {

    @Getter private final IPlayer player;
    private final Ladder ladder;
    @Getter private final QueueRange range;

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
}
