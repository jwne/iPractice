package com.shawckz.ipractice.queue.member;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.IPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
@RequiredArgsConstructor
public class UnrankedPartyQueueMember implements QueueMember {

    @NonNull @Getter private final Party party;

    @Override
    public Set<IPlayer> getPlayers() {
        Set<IPlayer> players = new HashSet<>();
        for(Player pl : party.getAllPlayers()){
            players.add(Practice.getCache().getIPlayer(pl));
        }
        return players;
    }
}
