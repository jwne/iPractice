package com.shawckz.ipractice.scoreboard.practice.label;

import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.XScoreboardTimer;

import java.text.DecimalFormat;

/**
 * Created by 360 on 9/12/2015.
 */
public class EnderpearlCooldownLabel extends XScoreboardTimer {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    private final IPlayer player;

    public EnderpearlCooldownLabel(XScoreboard scoreboard, int score, IPlayer player) {
        super(scoreboard, score, "", 0, 2);
        this.player = player;
    }

    @Override
    public void onUpdate() {
        if(player.getEnderpearl() > System.currentTimeMillis()){
            setVisible(true);
            setTime((player.getEnderpearl() - System.currentTimeMillis())/1000);
        }
        else{
            setVisible(false);
        }
    }

    @Override
    public void complete() {
        getScoreboard().removeLabel(this);
    }

    @Override
    public void updateTime() {
        if(getTime() > 0){
            setVisible(true);
            setTime(Double.parseDouble(df.format(getTime() - 0.1)));
        }
        else{
            setTime(0);
            setVisible(false);
        }
    }

    @Override
    public boolean isComplete() {
        return player.getEnderpearl() <= System.currentTimeMillis();
    }
}
