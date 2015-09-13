package com.shawckz.ipractice.queue.range;

/**
 * Created by 360 on 9/12/2015.
 */
public interface QueueRange<T extends QueueRange> {

    void incrementRange();

    boolean inRange(T range);

}
