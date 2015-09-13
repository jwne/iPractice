package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.type.RankedQueue;
import com.shawckz.ipractice.queue.type.UnrankedQueue;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
public class QueueManager {

    private boolean running = false;
    private final Map<QueueType,Queue> queues = new HashMap<>();

    public QueueManager(Practice instance) {
        registerQueue(QueueType.UNRANKED, new UnrankedQueue());
        registerQueue(QueueType.RANKED, new RankedQueue());
    }

    public void run(){
        if(running){
            throw new PracticeException("The QueueManager is already running");
        }
        else {
            running = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    for(Queue queue : queues.values()){
                        queue.run();
                    }
                }
            }.runTaskTimerAsynchronously(Practice.getPlugin(), 100L, 100L);
        }
    }

    public void registerQueue(QueueType type, Queue queue){
        queues.put(type, queue);
    }



    public boolean inQueue(IPlayer player){
        for(Queue queue : queues.values()){
            if(queue.inQueue(player)){
                return true;
            }
        }
        return false;
    }

    public void removeFromQueue(IPlayer player){
        Set<IPlayer> remove = new HashSet<>();
        remove.add(player);
        for(Queue queue : queues.values()){
            if(queue.inQueue(player)){
                queue.removeFromQueue(remove);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public Map<QueueType, Queue> getQueues() {
        return queues;
    }
}
