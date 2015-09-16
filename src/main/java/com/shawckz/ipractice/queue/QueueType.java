package com.shawckz.ipractice.queue;

import lombok.RequiredArgsConstructor;

/**
 * Created by 360 on 9/12/2015.
 */
@RequiredArgsConstructor
public enum QueueType {

    UNRANKED("Pairs you with random players."),
    RANKED("Pairs you with players with similar elo."),
    PING("Pairs you with players with similar ping."),
    UNRANKED_PARTY("Pairs you with random parties."),
    RANKED_PARTY("Pairs you with parties with similar average elo.");

    private final String description;

    public String getDescription() {
        return description;
    }

    public static QueueType fromString(String s){
        for(QueueType qt : values()){
            if(qt.toString().equalsIgnoreCase(s)){
                return qt;
            }
        }
        return null;
    }

}
