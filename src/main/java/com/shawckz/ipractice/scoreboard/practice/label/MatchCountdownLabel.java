package com.shawckz.ipractice.scoreboard.practice.label;

import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.XScoreboardTimer;
import org.bukkit.ChatColor;

/**
 * Created by 360 on 9/15/2015.
 */
public class MatchCountdownLabel extends XScoreboardTimer {

    private final Match match;

    public MatchCountdownLabel(XScoreboard scoreboard, int score, Match match) {
        super(scoreboard, score, "", 0, 10);
        this.match = match;
    }

    @Override
    public void onUpdate() {
        setValue(ChatColor.LIGHT_PURPLE+"Starting in: "+ChatColor.GREEN+match.getCountdown());
    }

    @Override
    public void complete() {
        getScoreboard().removeLabel(this);
    }

    @Override
    public void updateTime() {

    }

    @Override
    public boolean isComplete() {
        return match.isStarted();
    }
}
