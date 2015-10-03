package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.member.PartyQueueMember;
import com.shawckz.ipractice.queue.member.QueueMember;
import com.shawckz.ipractice.queue.member.RankedPartyQueueMember;
import com.shawckz.ipractice.queue.member.UnrankedPartyQueueMember;
import com.shawckz.ipractice.queue.type.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        registerQueue(QueueType.UNRANKED_PARTY, new UnrankedPartyQueue());
        registerQueue(QueueType.RANKED_PARTY, new RankedPartyQueue());
        registerQueue(QueueType.PING, new UnrankedPingQueue());
        registerQueue(QueueType.KITE, new KiteQueue());
        registerQueue(QueueType.UNRANKED_PARTY_SIZE, new UnrankedPartySizeQueue());
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

    public Queue getQueue(QueueType type){
        return queues.get(type);
    }

    public boolean inQueue(IPlayer player){
        for(Queue queue : queues.values()){
            if(queue.inQueue(player)){
                return true;
            }
        }
        return false;
    }

    public Queue getQueue(IPlayer player){
        for(Queue queue : queues.values()){
            if(queue.inQueue(player)){
                return queue;
            }
        }
        return null;
    }

    public void removeFromQueue(IPlayer player){
        for(Queue queue : queues.values()){
            if(queue.inQueue(player)){
                QueueMember member = queue.getMember(player);
                if(member instanceof PartyQueueMember){
                    PartyQueueMember pq = (PartyQueueMember) member;
                    if(pq.getParty().getLeader().equals(player.getName())){
                        queue.removeFromQueue(member);
                        for(IPlayer pl : member.getPlayers()){
                            pl.getPlayer().sendMessage(ChatColor.RED+"Your party was disbanded, so you were removed from the queue.");
                            pl.getScoreboard().update();
                        }
                        //If they are the leader, remove the whole party from the queue
                    }
                    else{
                        queue.removeFromQueue(member);
                        for(IPlayer pl : member.getPlayers()){
                            pl.getPlayer().sendMessage(ChatColor.RED+"A player left your party, so you were removed from the queue.");
                            pl.getScoreboard().update();
                        }
                        //If they aren't the leader, remove the whole party from the queue, and
                    }
                }
                else{
                    //They are in the queue alone, not in a party
                    queue.removeFromQueue(member);
                }
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
