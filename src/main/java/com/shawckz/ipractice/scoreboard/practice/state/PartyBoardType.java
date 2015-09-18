package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.practice.label.ValueLabel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

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

        valueLabels.add(new ValueLabel(scoreboard, player, 3, ChatColor.BLUE + "Party: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                if(player.getParty() != null){
                    return ChatColor.GREEN+player.getParty().getLeader();
                }
                return ChatColor.GREEN+"None";
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 3, ChatColor.BLUE + "Party ELO: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                if(player.getParty() != null) {
                    int i = 0;
                    int x = 0;
                    for (Player pl : player.getParty().getAllPlayers()) {
                        IPlayer ipl = Practice.getCache().getIPlayer(pl);
                        i += ipl.getAverageElo();
                        x++;
                    }
                    return ChatColor.GREEN + "" + (Math.round(i / x));
                }
                return ChatColor.GREEN+"None";
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
        return player.getState() == PlayerState.AT_SPAWN && !Practice.getQueueManager().inQueue(player) && player.getParty() != null;
    }
}
