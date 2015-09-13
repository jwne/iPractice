package com.shawckz.ipractice.queue.type;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.*;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueMatchSet;
import com.shawckz.ipractice.queue.member.QueueMember;
import com.shawckz.ipractice.queue.member.RankedQueueMember;
import com.shawckz.ipractice.queue.member.UnrankedQueueMember;
import com.shawckz.ipractice.queue.range.EloRange;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
public class RankedQueue extends Queue {

    @Override
    public Match createMatch(QueueMatchSet set) {
        MatchBuilder builder = Practice.getMatchManager().matchBuilder(set.getLadder());
        builder.setRanked(true);
        builder.registerTeam(new PracticeTeam(set.getAlpha().getName(), Team.ALPHA));
        builder.registerTeam(new PracticeTeam(set.getBravo().getName(), Team.BRAVO));
        for(IPlayer player : set.getAlpha().getPlayers()){
            builder.withPlayer(player.getPlayer(), set.getAlpha().getName());
        }
        for(IPlayer player : set.getBravo().getPlayers()){
            builder.withPlayer(player.getPlayer(), set.getBravo().getName());
        }
        return builder.build();
    }

    @Override
    public void addToQueue(Set<IPlayer> players, Ladder ladder) {
        for(IPlayer pl : players){
            RankedQueueMember queueMember = new RankedQueueMember(pl, ladder, new EloRange(pl.getElo(ladder)));
            getMembers().add(queueMember);
        }
    }

    @Override
    public void removeFromQueue(Set<IPlayer> players) {
        for(IPlayer pl : players){
            Iterator<QueueMember> it = getMembers().iterator();
            while(it.hasNext()){
                QueueMember member = it.next();
                if(member.getPlayers().contains(pl)){
                    getMembers().remove(member);
                }
            }
        }
    }

    @Override
    public boolean inRange(QueueMatchSet set) {
        return true;
    }
}
