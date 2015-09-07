package com.shawckz.ipractice.match;

public enum Team {

    ALPHA,
    BRAVO;

    public Team getOpposite(){
        if(this == ALPHA){
            return BRAVO;
        }
        return ALPHA;
    }

}
