package com.shawckz.ipractice.queue.range;

/**
 * Created by 360 on 9/12/2015.
 */
public class EloRange implements QueueRange {

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
        if(this.minElo < 0){
            this.minElo = 0;
        }
        if(this.maxElo > 5000){
            this.maxElo = 5000;
        }
    }

    @Override
    public boolean inRange(QueueRange range) {
        if(range instanceof EloRange){
            EloRange r = (EloRange) range;
            return minElo <= r.getElo() && maxElo >= r.getElo();
        }
        return false;
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

    @Override
    public String rangeToString() {
        return "["+minElo+" -> "+maxElo+"]";
    }
}
