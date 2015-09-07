package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

/**
 * Created by 360 on 09/05/2015.
 */

/**
 * The KDRange class
 * This is mostly a util class for use when trying to find a ranked match.
 */
@AllArgsConstructor
public class KDRange {

    @Getter @Setter private Ladder ladder;
    @Getter @Setter private double minKDR;
    @Getter @Setter private double maxKDR;

    public KDRange(IPlayer ip, Ladder ladder){
        this.ladder = ladder;
        this.minKDR = ip.getElo(ladder);
        this.maxKDR = ip.getElo(ladder);
        incrementRange();
    }

    public KDRange(Party party, Ladder ladder){
        this.ladder = ladder;
        this.minKDR = party.getAverageElo(ladder);
        this.maxKDR = party.getAverageElo(ladder);
        incrementRange();
    }

    /**
     * Increments the range by 0.2 in both directions (0.10 total gain)
     */
    public void incrementRange(){
        this.minKDR -= 20;
        this.maxKDR += 20;

        this.minKDR = round(this.minKDR);
        this.maxKDR = round(this.maxKDR);
    }

    /**
     * Formats said double to two decimal players
     * @param d The double
     * @return The rounded/formatted double
     */
    private double round(double d){
        return Double.parseDouble(new DecimalFormat("#.##").format(d));
    }

    /**
     * Returns if said KDR is in the KDRange
     * @param kdr the KDR
     * @return True if in range, false if else
     */
    public boolean inRange(double kdr){
        return kdr >= minKDR && kdr <= maxKDR;
    }

}
