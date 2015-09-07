package com.shawckz.ipractice.event;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.PracticeEventException;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class EventManager {

    private final Set<PracticeEvent> events = new HashSet<>();

    @Getter private PracticeEvent activeEvent = null;

    public EventManager(Practice instance) {

    }

    public boolean canStartEvent(){
        return activeEvent == null;
    }

    public void startEvent(PracticeEvent event){
        if(activeEvent != null){
            throw new PracticeEventException("Can not start an event when one is already running");
        }
        this.activeEvent = event;
        event.registerListener();
        event.startEvent();
    }

    public void endEvent(){
        if(activeEvent != null){
            activeEvent.endEvent();
            activeEvent.unregisterListener();
        }
        else{
            throw new PracticeEventException("Can not end an event when none is running");
        }
    }

}
