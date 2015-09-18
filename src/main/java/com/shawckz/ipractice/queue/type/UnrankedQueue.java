package com.shawckz.ipractice.queue.type;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.*;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueMatchSet;
import com.shawckz.ipractice.queue.QueueType;
import com.shawckz.ipractice.queue.member.UnrankedQueueMember;
import org.bukkit.Material;

/**
 * Created by 360 on 9/12/2015.
 */
public class UnrankedQueue extends Queue {

    public UnrankedQueue() {
        super(QueueType.UNRANKED);
    }

    @Override
    public Match createMatch(QueueMatchSet set) {
        MatchBuilder builder = Practice.getMatchManager().matchBuilder(set.getLadder());
        builder.setRanked(false);
        builder.registerTeam(new PracticeTeam(set.getAlpha().getName(), Team.ALPHA));
        builder.registerTeam(new PracticeTeam(set.getBravo().getName(), Team.BRAVO));
        for(IPlayer player : set.getAlpha().getPlayers()){
            builder.withPlayer(player.getPlayer(), set.getAlpha().getName());
        }
        for(IPlayer player : set.getBravo().getPlayers()){
            builder.withPlayer(player.getPlayer(), set.getBravo().getName());
        }
        return builder.build();
    }

    @Override
    public void addToQueue(IPlayer player, Ladder ladder) {
        UnrankedQueueMember queueMember = new UnrankedQueueMember(player, ladder);
        getMembers().add(queueMember);
    }

    @Override
    public Material getIcon() {
        return Material.IRON_HELMET;
    }

    @Override
    public boolean canJoin(IPlayer player) {
        return player.getState() == PlayerState.AT_SPAWN && player.getParty() == null;
    }

}
