package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.queue.member.QueueMember;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
public class QueueMatchSet {

    private final Ladder ladder;
    private final QueueMember alpha;
    private final QueueMember bravo;

    public QueueMatchSet(Ladder ladder, QueueMember alpha, QueueMember bravo) {
        this.ladder = ladder;
        this.alpha = alpha;
        this.bravo = bravo;
    }

    public Set<QueueMember> getAll(){
        Set<QueueMember> all = new HashSet<>();
        all.add(alpha);
        all.add(bravo);
        return all;
    }

    public Ladder getLadder() {
        return ladder;
    }

    public QueueMember getAlpha() {
        return alpha;
    }

    public QueueMember getBravo() {
        return bravo;
    }
}
