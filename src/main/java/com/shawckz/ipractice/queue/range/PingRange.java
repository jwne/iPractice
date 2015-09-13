package com.shawckz.ipractice.queue.range;

/**
 * Created by 360 on 9/12/2015.
 */
public class PingRange implements QueueRange {

    private int minPing;
    private int maxPing;
    private int ping;

    public PingRange(int ping) {
        this.ping = ping;
        this.minPing = ping;
        this.maxPing = ping;
        incrementRange();
    }

    @Override
    public void incrementRange() {
        this.minPing -= 10;
        this.maxPing -= 10;
    }

    @Override
    public boolean inRange(QueueRange range) {
        if(range instanceof PingRange){
            PingRange r = (PingRange) range;
            return minPing <= r.getPing() && maxPing >= r.getPing();
        }
        return false;
    }

    public int getMinPing() {
        return minPing;
    }

    public int getMaxPing() {
        return maxPing;
    }

    public int getPing() {
        return ping;
    }

    @Override
    public String rangeToString() {
        return "["+minPing+"ms -> "+maxPing+"ms]";
    }
}
