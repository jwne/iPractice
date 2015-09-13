package com.shawckz.ipractice.event;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public enum EventType {

    LMS,
    BRACKETS,
    RED_ROVER;


    private final String name;

    EventType(String name){
        this.name = name;
    }

    EventType(){
        this.name = WordUtils.capitalizeFully(this.toString().replaceAll("_", " "));
    }

    public String getName() {
        return name;
    }
}
