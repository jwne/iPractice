package com.shawckz.ipractice.match.participant;

import com.shawckz.ipractice.player.IPlayer;
import lombok.Data;

/**
 * Created by 360 on 9/7/2015.
 */
@Data
public class MatchPlayer {

    private final IPlayer player;
    private boolean alive = true;
    private boolean spectating = false;

}
