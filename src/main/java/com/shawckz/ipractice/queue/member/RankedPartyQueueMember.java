package com.shawckz.ipractice.queue.member;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.range.EloRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 5/13/2015.
 */

/**
 * The PartyQueueMember class
 * Used for representing a Party in the queue
 * Basically combines a party with range and ladder into one class.
 */
@AllArgsConstructor
public class RankedPartyQueueMember implements QueueMember, PartyQueueMember {

    @Getter @NonNull Party party;
    @Getter @NonNull EloRange range;
    @Getter @NonNull Ladder ladder;

    public double getAverageElo(){
        double averageScope = 0.0;
        for(Player p : party.getAllPlayers()){
            IPlayer ip = Practice.getCache().getIPlayer(p);
            averageScope += ip.getElo(ladder);
        }
        averageScope = Math.round((double)averageScope / (double)party.getAllMembers().toArray().length);
        return (double)averageScope;
    }

    @Override
    public Set<IPlayer> getPlayers() {
        Set<IPlayer> players = new HashSet<>();
        for(Player pl : party.getAllPlayers()){
            players.add(Practice.getCache().getIPlayer(pl));
        }
        return players;
    }

    @Override
    public String getName() {
        return party.getLeader()+"'s Party";
    }
}
