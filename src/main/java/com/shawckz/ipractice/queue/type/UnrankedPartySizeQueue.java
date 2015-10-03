package com.shawckz.ipractice.queue.type;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.MatchBuilder;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueMatchSet;
import com.shawckz.ipractice.queue.QueueType;
import com.shawckz.ipractice.queue.member.UnrankedPartyQueueMember;
import com.shawckz.ipractice.queue.range.UnrankedQueueRange;

import org.bukkit.Material;

/**
 * Created by 360 on 9/12/2015.
 */
public class UnrankedPartySizeQueue extends Queue implements PartyQueue {

    public UnrankedPartySizeQueue() {
        super(QueueType.UNRANKED_PARTY_SIZE);
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
        UnrankedPartyQueueMember queueMember = new UnrankedPartyQueueMember(player.getParty(), ladder, new UnrankedQueueRange());
        getMembers().add(queueMember);
    }

    @Override
    public Material getIcon() {
        return Material.GOLD_INGOT;
    }

    @Override
    public boolean canJoin(IPlayer player) {
        return player.getState() == PlayerState.AT_SPAWN && player.getParty() != null &&
                player.getParty().getLeader().equals(player.getName());
    }

    @Override
    public boolean inRange(QueueMatchSet set) {
        if(set.getAlpha() instanceof UnrankedPartyQueueMember && set.getBravo() instanceof UnrankedPartyQueueMember){
            UnrankedPartyQueueMember alpha = (UnrankedPartyQueueMember) set.getAlpha();
            UnrankedPartyQueueMember bravo = (UnrankedPartyQueueMember) set.getBravo();
            if(alpha.getParty().getAllMembers().size() == bravo.getParty().getAllMembers().size()){
                return true;
            }
        }
        return false;
    }



}
