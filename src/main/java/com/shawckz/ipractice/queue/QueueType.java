package com.shawckz.ipractice.queue;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;

/**
 * Created by 360 on 9/12/2015.
 */
public enum QueueType {

    UNRANKED("Pairs you with random players."),
    RANKED("Pairs you with players with similar elo."),
    PING("Pairs you with players with similar ping."),
    UNRANKED_PARTY("Pairs you with random parties."),
    UNRANKED_PARTY_SIZE("Pairs you with parties that have the same amount of players as your party."),
    RANKED_PARTY("Pairs you with parties with similar average elo."),
    KITE("Pairs you with random players.");

    private final String description;
    private final String name;

    QueueType(String description) {
        this.description = description;
        this.name = WordUtils.capitalizeFully(toString().replaceAll("_"," "));
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
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
