package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.practice.label.ValueLabel;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

public class PartyBoardType implements PracticeBoardType {

    private Set<ValueLabel> valueLabels = new HashSet<>();

    public PartyBoardType(XScoreboard scoreboard, IPlayer player) {

        valueLabels.add(new ValueLabel(scoreboard, player, 3, ChatColor.GOLD+""+ ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH +"", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return "-------------------"+ChatColor.GREEN+""+ChatColor.YELLOW;
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 2, ChatColor.GOLD + "Party: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                if(player.getParty() != null){
                    return ChatColor.AQUA+player.getParty().getLeader();
                }
                return ChatColor.AQUA+"None";
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
        return player.getState() == PlayerState.AT_SPAWN && !QueueSearch.inAnyQueue(player) && player.getParty() != null;
    }
}
