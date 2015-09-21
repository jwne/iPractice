package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.practice.label.ValueLabel;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public class SpawnBoardType implements PracticeBoardType {

    private Set<ValueLabel> valueLabels = new HashSet<>();

    public SpawnBoardType(XScoreboard scoreboard, IPlayer player) {

        valueLabels.add(new ValueLabel(scoreboard, player, 6, ChatColor.GOLD+""+ ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH +"", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return "-------------------"+ChatColor.GREEN+""+ChatColor.YELLOW;
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 5, ChatColor.BLUE + "Average ELO: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return ChatColor.GREEN+""+player.getAverageElo();
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 4, ChatColor.BLUE + "Ranked Matches: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return ChatColor.GREEN+""+player.getTotalMatchesAllLadders();
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 3, ChatColor.BLUE + "Ranked Kills: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return ChatColor.GREEN + "" + player.getKillsAllLadders();
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 2, ChatColor.BLUE + "Ranked Deaths: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return ChatColor.GREEN + "" + player.getDeathsAllLadders();
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 1, ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH, new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return "-------------------"+ChatColor.RED+"";
            }
        }));

        for(XLabel label : valueLabels){
            scoreboard.addLabel(label);
        }
    }

    @Override
    public void update(XScoreboard scoreboard) {
        for(ValueLabel label : valueLabels){
            if(!scoreboard.hasLabel(label)){
                scoreboard.addLabel(label);
            }
            label.setVisible(true);
            label.update();
        }
    }

    @Override
    public void remove(XScoreboard scoreboard) {
        for(ValueLabel label : valueLabels){
            scoreboard.removeLabel(label);
        }
    }

    @Override
    public boolean isApplicable(IPlayer player) {
        return player.getState() == PlayerState.AT_SPAWN && !Practice.getQueueManager().inQueue(player) &&
                player.getParty() == null && !player.isStaffMode();
    }
}
