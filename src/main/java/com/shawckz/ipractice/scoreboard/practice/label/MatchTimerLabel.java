package com.shawckz.ipractice.scoreboard.practice.label;

import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.XScoreboardTimer;
import org.bukkit.ChatColor;

/**
 * Created by 360 on 9/12/2015.
 */
public class MatchTimerLabel extends XScoreboardTimer {

    private final PracticeMatch match;

    private final String valueBase = ChatColor.BLUE+"Duration: "+ChatColor.GREEN;

    public MatchTimerLabel(XScoreboard scoreboard, int score, PracticeMatch match) {
        super(scoreboard, score, "", 0, 20);
        this.match = match;
    }

    @Override
    public void onUpdate() {
        if(match.isStarted()){
            setValue(valueBase+getTimeString());
        }
    }

    private String getTimeString(){
        int minutes = (int)(Math.round(getTime()) % 3600) / 60;
        int seconds = (int) Math.round(getTime()) % 60;
        String m = ""+minutes;
        if(minutes < 10){
            m = "0"+minutes;
        }
        String s = ""+seconds;
        if(seconds < 10){
            s = "0"+seconds;
        }
        return m+":"+s;
    }

    @Override
    public void complete() {
        getScoreboard().removeLabel(this);
    }

    @Override
    public void updateTime() {
        setTime(getTime() + 1);
    }

    @Override
    public boolean isComplete() {
        return match.isOver();
    }
}
