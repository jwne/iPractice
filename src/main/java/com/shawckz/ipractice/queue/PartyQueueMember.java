package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import org.bukkit.entity.Player;

/**
 * Created by 360 on 5/13/2015.
 */

/**
 * The PartyQueueMember class
 * Used for representing a Party in the queue
 * Basically combines a party with range and ladder into one class.
 */
@AllArgsConstructor
public class PartyQueueMember {

    @Getter @NonNull Party party;
    @Getter @NonNull KDRange range;
    @Getter @NonNull Ladder ladder;

    public double getScopeAverage(){
        double averageScope = 0.0;
        for(Player p : party.getAllPlayers()){
            IPlayer ip = Practice.getCache().getIPlayer(p);
            averageScope += ip.getElo(ladder);
        }
        averageScope = Math.round((double)averageScope / (double)party.getAllMembers().toArray().length);
        return (double)averageScope;
    }

}
