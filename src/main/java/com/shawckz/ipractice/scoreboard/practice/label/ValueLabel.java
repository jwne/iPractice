package com.shawckz.ipractice.scoreboard.practice.label;

import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.XScoreboardLabel;

public class ValueLabel extends XScoreboardLabel {

    private final String key;
    private final CallableValue value;
    private final IPlayer iPlayer;

    public ValueLabel(XScoreboard scoreboard, IPlayer iPlayer, int score, String key, CallableValue value) {
        super(scoreboard, score, "");
        this.key = key;
        this.value = value;
        this.iPlayer = iPlayer;
    }

    @Override
    public void update(){
        setValue(key+value.call(iPlayer));
        super.update();
    }

    public interface CallableValue {
        String call(IPlayer player);
    }

}
