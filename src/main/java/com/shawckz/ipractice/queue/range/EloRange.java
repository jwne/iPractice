package com.shawckz.ipractice.queue.range;

/**
 * Created by 360 on 9/12/2015.
 */
public class EloRange implements QueueRange<EloRange> {

    private int minElo;
    private int maxElo;
    private int elo;

    public EloRange(int elo) {
        this.elo = elo;
        this.minElo = elo;
        this.maxElo = elo;
        incrementRange();
    }

    @Override
    public void incrementRange() {
        this.minElo = this.minElo - 50;
        this.maxElo = this.maxElo + 50;
    }

    @Override
    public boolean inRange(EloRange range) {
        return minElo <= range.getElo() && maxElo >= range.getElo();
    }

    public int getElo() {
        return elo;
    }

    public int getMaxElo() {
        return maxElo;
    }

    public int getMinElo() {
        return minElo;
    }
}
