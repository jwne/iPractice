package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.label.BasicLabel;
import com.shawckz.ipractice.scoreboard.practice.label.EnderpearlCooldownLabel;
import com.shawckz.ipractice.scoreboard.practice.label.MatchCountdownLabel;
import com.shawckz.ipractice.scoreboard.practice.label.MatchTimerLabel;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public class MatchBoardType implements PracticeBoardType {

    private Set<XLabel> labels = new HashSet<>();

    private MatchTimerLabel matchTimerLabel = null;
    private EnderpearlCooldownLabel enderpearlCooldownLabel = null;
    private MatchCountdownLabel matchCountdownLabel = null;
    private final IPlayer player;

    public MatchBoardType(XScoreboard scoreboard, IPlayer player) {
        this.player = player;
    }

    @Override
    public void update(XScoreboard scoreboard) {
        remove(scoreboard);
        Match match = Practice.getMatchManager().getMatch(player);

        labels.add(new BasicLabel(scoreboard, 5, ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH + "-------------------" + ChatColor.GREEN + "" + ChatColor.YELLOW));

        labels.add(new BasicLabel(scoreboard, 1, ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH + "-------------------" + ChatColor.RED));


        if(this.matchTimerLabel == null){
            this.matchTimerLabel = new MatchTimerLabel(scoreboard, 4, match);
            labels.add(matchTimerLabel);
        }
        if(this.matchCountdownLabel == null){
            this.matchCountdownLabel = new MatchCountdownLabel(scoreboard, 3, match);
            labels.add(matchCountdownLabel);
        }
        if(this.enderpearlCooldownLabel == null){
            this.enderpearlCooldownLabel = new EnderpearlCooldownLabel(scoreboard, 2, player);
            labels.add(enderpearlCooldownLabel);
        }

        for(XLabel label : labels){
            label.setVisible(true);
            label.update();
        }

        if(match.isStarted() && !match.isOver()){
            if(!matchTimerLabel.isRunning()) {
                matchTimerLabel.start();
            }
        }
        if(!match.isStarted() && match.getCountdown() > 0){
            if(!matchCountdownLabel.isRunning()){
                this.matchCountdownLabel.start();
            }
        }

        if(!this.enderpearlCooldownLabel.isRunning()){
            this.enderpearlCooldownLabel.start();
        }
    }

    @Override
    public void remove(XScoreboard scoreboard) {
        for(XLabel label : labels){
            scoreboard.removeLabel(label);
        }
        //labels.clear(); Going to remove this so that they will be removed (hopefully) properly
        if(this.matchTimerLabel != null){
            if(this.matchTimerLabel.isRunning()){
                this.matchTimerLabel.stop();
            }
        }
        if(this.enderpearlCooldownLabel != null){
            if(this.enderpearlCooldownLabel.isRunning()){
                this.enderpearlCooldownLabel.stop();
            }
        }
        if(this.matchCountdownLabel != null){
            if(this.matchCountdownLabel.isRunning()){
                this.matchCountdownLabel.start();
            }
        }
        Match match = Practice.getMatchManager().getMatch(player);
        if(match == null || match.isOver() || !match.isStarted()){
            labels.clear();
            this.matchTimerLabel = null;
            this.enderpearlCooldownLabel = null;
            this.matchCountdownLabel = null;
        }
    }

    @Override
    public boolean isApplicable(IPlayer player) {
        return player.getState() == PlayerState.IN_MATCH;
    }
}
