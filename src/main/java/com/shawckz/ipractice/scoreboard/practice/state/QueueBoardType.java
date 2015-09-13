package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.practice.label.ValueLabel;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class QueueBoardType implements PracticeBoardType {

    private List<ValueLabel> valueLabels = new ArrayList<>();

    public QueueBoardType(XScoreboard scoreboard, IPlayer player) {

        valueLabels.add(new ValueLabel(scoreboard, player, 4, ChatColor.GOLD + "" + ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH + "", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return "-------------------" + ChatColor.GREEN + "" + ChatColor.YELLOW;
            }
        }));


        valueLabels.add(new ValueLabel(scoreboard, player, 3, ChatColor.GOLD+"Ladder: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                if(Practice.getQueueManager().inQueue(player)){
                    return ChatColor.AQUA+Practice.getQueueManager().getQueue(player).getMember(player).getLadder().getName();
                }
                return "";
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 2, ChatColor.GOLD+"Range: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                if(Practice.getQueueManager().inQueue(player)){
                    Queue queue = Practice.getQueueManager().getQueue(player);
                    return ChatColor.AQUA+queue.getMember(player).getRange().rangeToString();
                }
                return "";
            }
        }));


        valueLabels.add(new ValueLabel(scoreboard, player, 1, ChatColor.GRAY + "" +
                ChatColor.STRIKETHROUGH, new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                return "-------------------" + ChatColor.RED + "";
            }
        }));

        for (XLabel label : valueLabels) {
            scoreboard.addLabel(label);
        }
    }

    @Override
    public void update(XScoreboard scoreboard) {
        remove(scoreboard);
        for (ValueLabel label : valueLabels) {
            label.setVisible(true);
            label.update();
        }
    }

    @Override
    public void remove(XScoreboard scoreboard) {
        for (ValueLabel label : valueLabels) {
            scoreboard.removeLabel(label);
            label.setVisible(false);
        }
    }

    @Override
    public boolean isApplicable(IPlayer player) {
        return player.getState() == PlayerState.AT_SPAWN && Practice.getQueueManager().inQueue(player);
    }
}
