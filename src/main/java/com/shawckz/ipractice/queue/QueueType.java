package com.shawckz.ipractice.queue;

/**
 * Created by 360 on 9/12/2015.
 */
public enum QueueType {

    UNRANKED,
    RANKED,
    UNRANKED_PING,
    RANKED_PING,
    UNRANKED_PARTY,
    RANKED_PARTY;

    public static QueueType fromString(String s){
        for(QueueType qt : values()){
            if(qt.toString().equalsIgnoreCase(s)){
                return qt;
            }
        }
        return null;
    }

}
