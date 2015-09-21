package com.shawckz.ipractice.scoreboard.practice.label;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.XScoreboardTimer;
import org.bukkit.ChatColor;


/**
 * Created by 360 on 9/12/2015.
 */
public class EnderpearlCooldownLabel extends XScoreboardTimer {

    private final IPlayer player;

    public EnderpearlCooldownLabel(XScoreboard scoreboard, int score, IPlayer player) {
        super(scoreboard, score, "", 0, 10);
        this.player = player;
    }

    @Override
    public void onUpdate() {
        if(player.getEnderpearl() > System.currentTimeMillis()){
            setVisible(true);
            setValue(ChatColor.BLUE+"Enderpearl: "+ChatColor.GREEN+((player.getEnderpearl() - System.currentTimeMillis())/1000)+"s");
        }
        else{
            setVisible(false);
            getScoreboard().removeLabel(this);
        }
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
        PracticeMatch match = Practice.getMatchManager().getMatch(player);
        return match == null || match.isOver();
    }
}
