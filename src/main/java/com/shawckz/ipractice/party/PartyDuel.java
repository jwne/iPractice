package com.shawckz.ipractice.party;

import com.shawckz.ipractice.ladder.Ladder;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by 360 on 5/14/2015.
 */

/**
 * The PartyDuel class
 * Used to represent a party duel request
 * from one party to another.
 */
@AllArgsConstructor
public class PartyDuel {

    @Getter
    private Party sender;
    @Getter
    private Party recipient;
    @Getter
    private Ladder ladder;

}
