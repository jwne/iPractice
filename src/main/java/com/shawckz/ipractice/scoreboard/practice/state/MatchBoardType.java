package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.MatchType;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.internal.label.BasicLabel;
import com.shawckz.ipractice.scoreboard.practice.label.EnderpearlCooldownLabel;
import com.shawckz.ipractice.scoreboard.practice.label.MatchTimerLabel;
import com.shawckz.ipractice.scoreboard.practice.label.ValueLabel;

import org.bukkit.Bukkit;
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
        final PracticeMatch practiceMatch = Practice.getMatchManager().getMatch(player);

        if(practiceMatch != null) {

            if (practiceMatch.getType() == MatchType.NORMAL || practiceMatch.getType() == MatchType.KITE) {

                if (this.matchTimerLabel == null) {
                    this.matchTimerLabel = new MatchTimerLabel(scoreboard, 3, practiceMatch);
                    labels.add(matchTimerLabel);
                }
                if (this.enderpearlCooldownLabel == null) {
                    this.enderpearlCooldownLabel = new EnderpearlCooldownLabel(scoreboard, 2, player);
                    labels.add(enderpearlCooldownLabel);
                }

           //     if((this.enderpearlCooldownLabel.isVisible() && this.enderpearlCooldownLabel.isRunning())
          //              || (this.matchTimerLabel.isVisible() && this.matchTimerLabel.isRunning() && !this.matchTimerLabel.isComplete())) {
                    labels.add(new BasicLabel(scoreboard, 4, ChatColor.GRAY + "" +
                            ChatColor.STRIKETHROUGH + "-------------------" + ChatColor.GREEN + "" + ChatColor.YELLOW));

                    labels.add(new BasicLabel(scoreboard, 1, ChatColor.GRAY + "" +
                            ChatColor.STRIKETHROUGH + "-------------------" + ChatColor.RED));
           //     }

                for (XLabel label : labels) {
                    label.setVisible(true);
                    label.update();
                }

                if (practiceMatch.isStarted() && !practiceMatch.isOver()) {
                    if (!matchTimerLabel.isRunning()) {
                        matchTimerLabel.start();
                    }
                }
                if (!this.enderpearlCooldownLabel.isRunning()) {
                    this.enderpearlCooldownLabel.start();
                }
            }
        }
    }

    @Override
    public void remove(XScoreboard scoreboard) {
        for(XLabel label : labels){
            scoreboard.removeLabel(label);
        }
        PracticeMatch practiceMatch = Practice.getMatchManager().getMatch(player);
        //labels.clear(); Going to remove this so that they will be removed (hopefully) properly
        if(practiceMatch != null && (practiceMatch.getType() == MatchType.NORMAL || practiceMatch.getType() == MatchType.KITE)) {
            if (this.matchTimerLabel != null) {
                if (this.matchTimerLabel.isRunning()) {
                    this.matchTimerLabel.stop();
                }
                matchTimerLabel.complete();
            }
            if (this.enderpearlCooldownLabel != null) {
                if (this.enderpearlCooldownLabel.isRunning()) {
                    this.enderpearlCooldownLabel.stop();
                }
                enderpearlCooldownLabel.complete();
            }
            labels.clear();
            this.matchTimerLabel = null;
            this.enderpearlCooldownLabel = null;
        }
    }

    @Override
    public boolean isApplicable(IPlayer player) {
        return player.getState() == PlayerState.IN_MATCH && Practice.getMatchManager().inMatch(player)
                && Practice.getMatchManager().getMatch(player).isStarted();
    }
}
