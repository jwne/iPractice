package com.shawckz.ipractice.queue.range;

/**
 * Created by 360 on 9/12/2015.
 */
public class UnrankedQueueRange implements QueueRange {

    @Override
    public void incrementRange() {

    }

    @Override
    public boolean inRange(QueueRange range) {
        return true;
    }

    @Override
    public String rangeToString() {
        return "[Any]";
    }
}
