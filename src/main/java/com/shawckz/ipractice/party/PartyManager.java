package com.shawckz.ipractice.party;

import com.shawckz.ipractice.Practice;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 360 on 5/13/2015.
 */

/**
 * The PartyManager class
 * Used to manage all existing parties, and get
 * what parties players are in.
 */
public class PartyManager {

    public PartyManager(Practice instance) {}

    @Getter private List<Party> parties = new ArrayList<>();

    /**
     * Register a party into the party list
     * @param party The Party
     */
    public void register(Party party){
        if(!parties.contains(party)){
            parties.add(party);
        }
    }

    /**
     * Unregister a party from the party list
     * @param party The party
     */
    public void unregister(Party party){
        if(parties.contains(party)){
            parties.remove(party);
        }
    }

    /**
     * Gets the party a player is in
     * @param p The Player
     * @return The party, null if not in a party
     */
    public Party getParty(Player p){
        if(p == null) return null;
        for(Party party : getParties()){
            if(party.getAllMembers().contains(p.getName())){
                return party;
            }
        }
        return null;
    }

}
