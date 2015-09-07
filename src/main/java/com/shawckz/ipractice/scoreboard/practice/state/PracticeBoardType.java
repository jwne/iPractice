package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;

public interface PracticeBoardType {

    void update(XScoreboard scoreboard);

    void remove(XScoreboard scoreboard);

    boolean isApplicable(IPlayer player);

}
