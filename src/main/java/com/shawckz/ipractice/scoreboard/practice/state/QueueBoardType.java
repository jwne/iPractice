package com.shawckz.ipractice.scoreboard.practice.state;

import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.PartyQueueMember;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueMember;
import com.shawckz.ipractice.queue.QueueType;
import com.shawckz.ipractice.scoreboard.internal.XLabel;
import com.shawckz.ipractice.scoreboard.internal.XScoreboard;
import com.shawckz.ipractice.scoreboard.practice.label.ValueLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;

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
                if(Queue.contains(player.getName(), QueueType.RANKED)){
                    QueueMember qm = Queue.getQueueMember(player.getName(), QueueType.RANKED);
                    if(qm != null){
                        return ChatColor.AQUA+qm.getRange().getLadder().getName();
                    }
                }
                else if (Queue.contains(player.getName(), QueueType.UNRANKED)){
                    QueueMember qm = Queue.getQueueMember(player.getName(), QueueType.UNRANKED);
                    if(qm != null){
                        return ChatColor.AQUA+qm.getRange().getLadder().getName();
                    }
                }
                else if (Queue.contains(player.getName(), QueueType.RANKED_PARTY)){
                    PartyQueueMember qm = Queue.getPartyQueueMember(player.getName(), QueueType.RANKED_PARTY);
                    if(qm != null){
                        return ChatColor.AQUA+qm.getRange().getLadder().getName();
                    }
                }
                else if (Queue.contains(player.getName(), QueueType.PARTY)){
                    PartyQueueMember qm = Queue.getPartyQueueMember(player.getName(), QueueType.PARTY);
                    if(qm != null){
                        return ChatColor.AQUA+qm.getRange().getLadder().getName();
                    }

                }
                return "";
            }
        }));

        valueLabels.add(new ValueLabel(scoreboard, player, 2, ChatColor.GOLD+"Range: ", new ValueLabel.CallableValue() {
            @Override
            public String call(IPlayer player) {
                if(Queue.contains(player.getName(), QueueType.RANKED)){
                    QueueMember qm = Queue.getQueueMember(player.getName(), QueueType.RANKED);
                    if(qm != null){
                        return ChatColor.AQUA+
                                "["+qm.getRange().getMinKDR()+" -> "+qm.getRange().getMaxKDR()+"]";
                    }
                }
                else if (Queue.contains(player.getName(), QueueType.UNRANKED)){
                    QueueMember qm = Queue.getQueueMember(player.getName(), QueueType.UNRANKED);
                    if(qm != null){
                        return ChatColor.AQUA+"[N/A]";
                    }
                }
                else if (Queue.contains(player.getName(), QueueType.RANKED_PARTY)){
                    PartyQueueMember qm = Queue.getPartyQueueMember(player.getName(), QueueType.RANKED_PARTY);
                    if(qm != null){
                        return ChatColor.AQUA+
                                "["+qm.getRange().getMinKDR()+" -> "+qm.getRange().getMaxKDR()+"]";
                    }
                }
                else if (Queue.contains(player.getName(), QueueType.PARTY)){
                    PartyQueueMember qm = Queue.getPartyQueueMember(player.getName(), QueueType.PARTY);
                    if(qm != null){
                        return ChatColor.AQUA+"[N/A]";
                    }

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
            label.updateValue();
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
        return player.getState() == PlayerState.AT_SPAWN && Queue.inAnyQueue(player);
    }
}
