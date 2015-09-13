package com.shawckz.ipractice.queue.range;

/**
 * Created by 360 on 9/12/2015.
 */
public interface QueueRange {

    void incrementRange();

    boolean inRange(QueueRange range);

    String rangeToString();

}
