package com.shawckz.ipractice.queue.member;

import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.range.QueueRange;

import java.util.Set;

/**
 * Created by 360 on 09/05/2015.
 */

/**
 * The QueueMember class, just a simple class to combine a CPlayer with a KDRange
 * in order to make the queue system a lot easier.
 */
public interface QueueMember {

    Set<IPlayer> getPlayers();

    Ladder getLadder();

    String getName();

    QueueRange getRange();

}
