package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.label.BasicLabel;
import com.shawckz.ipractice.scoreboard.practice.label.EnderpearlCooldownLabel;
import com.shawckz.ipractice.scoreboard.practice.label.MatchTimerLabel;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public class MatchBoardType implements PracticeBoardType {

    private Set<XLabel> labels = new HashSet<>();

    private MatchTimerLabel matchTimerLabel = null;
    private EnderpearlCooldownLabel enderpearlCooldownLabel = null;
    private final IPlayer player;

    public MatchBoardType(XScoreboard scoreboard, IPlayer player) {
        this.player = player;
    }

    @Override
    public void update(XScoreboard scoreboard) {
        remove(scoreboard);
        Match match = Practice.getMatchManager().getMatch(player);

        labels.add(new BasicLabel(scoreboard, 4, ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH + "-------------------" + ChatColor.GREEN + "" + ChatColor.YELLOW));

        labels.add(new BasicLabel(scoreboard, 2, ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH + "-------------------" + ChatColor.RED));


        this.matchTimerLabel = new MatchTimerLabel(scoreboard, 3, match);
        this.enderpearlCooldownLabel = new EnderpearlCooldownLabel(scoreboard, 1, player);

        labels.add(matchTimerLabel);

        for(XLabel label : labels){
            label.setVisible(true);
            label.update();
        }

        if(match.isStarted() && !match.isOver()){
            matchTimerLabel.start();
            enderpearlCooldownLabel.start();
        }

    }

    @Override
    public void remove(XScoreboard scoreboard) {
        for(XLabel label : labels){
            scoreboard.removeLabel(label);
        }
        labels.clear();
        if(this.matchTimerLabel != null){
            if(this.matchTimerLabel.isRunning()){
                this.matchTimerLabel.stop();
            }
            this.matchTimerLabel = null;
        }
        if(this.enderpearlCooldownLabel != null){
            if(this.enderpearlCooldownLabel.isRunning()){
                this.enderpearlCooldownLabel.stop();
            }
            this.enderpearlCooldownLabel = null;
        }
    }

    @Override
    public boolean isApplicable(IPlayer player) {
        return player.getState() == PlayerState.IN_MATCH;
    }
}
