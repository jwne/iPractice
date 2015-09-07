package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.type.BasicMatch;
import com.shawckz.ipractice.match.type.PartyMatch;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.party.PartyManager;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import lombok.Getter;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by 360 on 09/05/2015.
 */

/**
 * The Queue Class
 * This class is intended for ranked & unranked (and possibly more?) 1v1 matching.
 * Simply call #addToQueue and let this class do the work.
 */
public class Queue {

    @Getter
    private static Map<QueueType,ConcurrentLinkedQueue<QueueMember>> queue = new TreeMap<>();
    @Getter
    private static ConcurrentLinkedQueue<PartyQueueMember> partyQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private static ConcurrentLinkedQueue<PartyQueueMember> rankedPartyQueue = new ConcurrentLinkedQueue<>();

    public static void remove(IPlayer p){
        for(QueueType m : QueueType.values()){
            removeFromQueue(p,m);
        }
        if(p.getParty() != null){
            if(p.getParty().getLeader().equalsIgnoreCase(p.getName())){
                removeFromPartyQueue(p.getParty());
                removeFromRankedPartyQueue(p.getParty());
            }
        }
    }

    static {
        queue.put(QueueType.RANKED,new ConcurrentLinkedQueue<QueueMember>());
        queue.put(QueueType.UNRANKED,new ConcurrentLinkedQueue<QueueMember>());

        new BukkitRunnable(){
            @Override
            public void run() {
                findMatches(QueueType.RANKED);
                findMatches(QueueType.UNRANKED);
                findMatches(QueueType.PARTY);
                findMatches(QueueType.RANKED_PARTY);
            }
        }.runTaskTimer(Practice.getPlugin(),100L,100L);
    }

    /**
     * Attempts to find matches for current players in queue by iterating through players to
     * try and find a potential opponent.
     * @param type The QueueType to find for
     */
    private static void findMatches(QueueType type){
        if(type == QueueType.RANKED){
            Iterator<QueueMember> it = queue.get(type).iterator();
            while(it.hasNext()){
                if(!it.hasNext()) break;
                boolean found = false;
                QueueMember q = it.next();
                Iterator<QueueMember> qt = queue.get(type).iterator();
                while(it.hasNext() && !found){
                    if(!qt.hasNext()) break;
                    QueueMember qp = qt.next();
                    if(qp == null) continue;
                    if(qp.getPlayer().getState() != PlayerState.AT_SPAWN || q.getPlayer().getState() != PlayerState.AT_SPAWN) continue;
                    if(qp.getRange().getLadder() != q.getRange().getLadder()) continue;
                    if(qp.getRange().inRange(q.getPlayer().getElo(qp.getRange().getLadder())) && !qp.getPlayer().getName().equalsIgnoreCase(q.getPlayer().getName())){
                        queue.get(type).remove(q);
                        queue.get(type).remove(qp);
                        found = true;
                        startMatch(QueueType.RANKED,qp.getPlayer(),q.getPlayer(),q.getRange().getLadder());
                    }
                }
                if(!found){
                    q.getRange().incrementRange();
                    Player p = Bukkit.getPlayerExact(q.getPlayer().getName());
                    if(p!=null){
                        p.sendMessage(ChatColor.GOLD+"Your Elo is "+ChatColor.LIGHT_PURPLE+
                                q.getPlayer().getElo(q.getRange().getLadder())+
                                ChatColor.GOLD+", searching in range "+ChatColor.BLUE+"["+q.getRange().getMinKDR() +
                                " --> "+q.getRange().getMaxKDR()+"]");
                        IPlayer ip = Practice.getCache().getIPlayer(p);
                        ip.getScoreboard().update();
                    }
                    else{
                        queue.get(type).remove(q);
                    }
                }
            }
        }
        else if (type == QueueType.UNRANKED){
            Iterator<QueueMember> it = queue.get(type).iterator();
            while (it.hasNext()){
                QueueMember q = it.next();

                Iterator<QueueMember> qt = queue.get(type).iterator();

                if(qt.hasNext()){
                    QueueMember qn = qt.next();
                    if(qn.getRange().getLadder() != q.getRange().getLadder()) continue;
                    if(qn.getPlayer().getName().equals(q.getPlayer().getName()))continue;
                    queue.get(type).remove(q);
                    queue.get(type).remove(qn);
                    startMatch(QueueType.UNRANKED,q.getPlayer(),qn.getPlayer(),q.getRange().getLadder());
                }
                else{
                    Player p = Bukkit.getPlayerExact(q.getPlayer().getName());
                    if(p!=null){
                        p.sendMessage(ChatColor.GOLD+"Waiting for another player to join the unranked queue");
                    }
                }
            }
        }
        else if (type == QueueType.PARTY){

            Iterator<PartyQueueMember> it = partyQueue.iterator();

            while(it.hasNext()){
                PartyQueueMember partyQueueMember = it.next();
                Party party = partyQueueMember.getParty();
                if(it.hasNext()){
                    PartyQueueMember partyQueueMember2 = it.next();
                    Party party2 = partyQueueMember2.getParty();

                    if(partyQueueMember.getLadder() != partyQueueMember2.getLadder()) continue;

                    partyQueue.remove(partyQueueMember);
                    partyQueue.remove(partyQueueMember2);

                    PartyMatch m = new PartyMatch(partyQueueMember.getLadder(),party,party2, false);
                    m.start();
                }
                else{
                    party.msg(ChatColor.GOLD+"Waiting for another party to join the party queue");
                }

            }
        }
        else if (type == QueueType.RANKED_PARTY){

            Iterator<PartyQueueMember> it = rankedPartyQueue.iterator();

            while(it.hasNext()){
                PartyQueueMember p = it.next();
                if(it.hasNext()){
                    if(p.getParty().getAllMembers().toArray().length != 2){
                        removeFromRankedPartyQueue(p.getParty());
                        for(Player pl : p.getParty().getAllPlayers()){
                            IPlayer cp = Practice.getCache().getIPlayer(pl);
                            if(cp.getState() == PlayerState.AT_SPAWN){
                                cp.sendToSpawn();
                            }
                        }
                        continue;
                    }
                }
                if(it.hasNext()){
                    PartyQueueMember p2 = it.next();

                    if((p.getRange().inRange(p2.getRange().getMaxKDR()) || p.getRange().inRange(p2.getRange().getMinKDR())) && p.getLadder() == p2.getLadder()){
                        rankedPartyQueue.remove(p2);
                        rankedPartyQueue.remove(p);

                        PartyMatch m = new PartyMatch(p2.getLadder(),p.getParty(),p2.getParty(), true);
                        m.start();
                    }
                    else{
                        p.getRange().incrementRange();
                        p.getParty().msg(ChatColor.GOLD+"Your Party Elo is "+ChatColor.AQUA+p.getScopeAverage()
                                +ChatColor.GOLD+", searching in range "+ChatColor.BLUE+"["+p.getRange().getMinKDR()
                                +" --> "+p.getRange().getMaxKDR()+"]");
                        for(Player pl : p.getParty().getAllPlayers()){
                            IPlayer ip = Practice.getCache().getIPlayer(pl);
                            ip.getScoreboard().update();
                        }
                    }
                }
                else{
                    p.getRange().incrementRange();
                    p.getParty().msg(ChatColor.GOLD+"Your Party Elo is "+ChatColor.AQUA+
                            p.getScopeAverage()+ChatColor.GOLD+", searching in range "+ChatColor.BLUE+"["+
                            p.getRange().getMinKDR()+" --> "+p.getRange().getMaxKDR()+"]");
                    for(Player pl : p.getParty().getAllPlayers()){
                        IPlayer ip = Practice.getCache().getIPlayer(pl);
                        ip.getScoreboard().update();
                    }
                }
            }
        }
    }

    /**
     * Add a party to the Ranked Party Queue
     * @param party The party
     * @param ladder the ladder
     */
    public static void addToRankedPartyQueue(Party party,Ladder ladder){
        if(!inRankedPartyQueue(party)){

            PartyQueueMember partyQueueMember = new PartyQueueMember(party,new KDRange(party,ladder),ladder);
            rankedPartyQueue.add(partyQueueMember);
            party.msg(ChatColor.GOLD+"Joined the "+ChatColor.RED+"ranked party"+ChatColor.GOLD+" match queue.");
            party.msg(ChatColor.GOLD+"Your Party Elo is "+ChatColor.LIGHT_PURPLE+partyQueueMember.getScopeAverage()+
                    ChatColor.GOLD+", searching in range "+ChatColor.BLUE+"["+partyQueueMember.getRange().getMinKDR()
                    +" --> "+partyQueueMember.getRange().getMaxKDR()+"]");
            for(Player pl : party.getAllPlayers()){
                IPlayer ip = Practice.getCache().getIPlayer(pl);
                ip.getScoreboard().update();
            }
        }
    }

    /**
     * Remove a party from the Ranked Party Queue
     * @param party The party
     */
    public static void removeFromRankedPartyQueue(Party party){
        if(inRankedPartyQueue(party)){
            Iterator<PartyQueueMember> it = rankedPartyQueue.iterator();
            while(it.hasNext()){
                if(it.hasNext()) {
                    PartyQueueMember partyQueueMember = it.next();
                    if (partyQueueMember.getParty().getLeader().equals(party.getLeader())) {
                        if (rankedPartyQueue.contains(partyQueueMember)) {
                            rankedPartyQueue.remove(partyQueueMember);
                        }
                        break;
                    }
                }
            }
            for(Player pl : party.getAllPlayers()){
                IPlayer ip = Practice.getCache().getIPlayer(pl);
                ip.getScoreboard().update();
            }
        }
    }

    /**
     * Get if a party is in the ranked party queue
     * @param party The Party
     * @return true if in the queue, false if not
     */
    public static boolean inRankedPartyQueue(Party party){
        for(PartyQueueMember partyQueueMember : rankedPartyQueue){
            if(party.getLeader().equals(partyQueueMember.getParty().getLeader())){
                return true;
            }
        }
        return false;
    }

    /**
     * Add a party to the normal party queue
     * @param party The party
     * @param ladder The ladder
     */
    public static void addToPartyQueue(Party party,Ladder ladder){
        if(!inPartyQueue(party)){
            PartyQueueMember partyQueueMember = new PartyQueueMember(party,new KDRange(party,ladder),ladder);
            partyQueue.add(partyQueueMember);
            party.msg(ChatColor.GOLD+"Waiting for another party to join the party queue");
            for(Player pl : party.getAllPlayers()){
                IPlayer ip = Practice.getCache().getIPlayer(pl);
                ip.getScoreboard().update();
            }
        }
    }

    /**
     * Get if a party is in the normal party queue
     * @param party The Party
     * @return true if in the queue, false if not
     */
    public static boolean inPartyQueue(Party party){
        for(PartyQueueMember p : partyQueue){
            if(p.getParty().getLeader().equals(party.getLeader())){
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a party from the normal party queue
     * @param party The party
     */
    public static void removeFromPartyQueue(Party party){
        if(inPartyQueue(party)){
            Iterator<PartyQueueMember> it = partyQueue.iterator();
            while(it.hasNext()){
                PartyQueueMember partyQueueMember = it.next();
                if(partyQueueMember.getParty().getLeader().equals(party.getLeader())){
                    partyQueue.remove(partyQueueMember);
                }
            }
            for(Player pl : party.getAllPlayers()){
                IPlayer ip = Practice.getCache().getIPlayer(pl);
                ip.getScoreboard().update();
            }
        }
    }

    /**
     * Creates a match using two players and the said type
     * @param type The Type
     * @param alpha Player 1
     * @param bravo Player 2
     */
    private static void startMatch(QueueType type,IPlayer alpha, IPlayer bravo,Ladder ladder){
        if(type == QueueType.RANKED || type == QueueType.RANKED_PARTY){
            Match m = new BasicMatch(ladder,alpha.getPlayer(),bravo.getPlayer(), true);
            m.start();
        }
        else{
            Match m = new BasicMatch(ladder,alpha.getPlayer(),bravo.getPlayer(), false);
            m.start();
        }
        alpha.getScoreboard().update();
        bravo.getScoreboard().update();
    }

    /**
     * Adds a player to the queue
     * @param IPlayer The Player to add
     * @param type The QueueType to add to
     */
    public static void addToQueue(IPlayer IPlayer, QueueType type,Ladder ladder){
        if(!contains(IPlayer.getName(),type)){
            IPlayer.getPlayer().sendMessage(ChatColor.GOLD+"Joined the "+ChatColor.LIGHT_PURPLE+
                    type.toString().toLowerCase()+ChatColor.GOLD+" match queue.");
            QueueMember queuePlayer = new QueueMember(IPlayer,new KDRange(IPlayer,ladder));
            queue.get(type).add(queuePlayer);
            IPlayer.getScoreboard().update();
            if(type == QueueType.RANKED){
                IPlayer.getPlayer().sendMessage(ChatColor.GOLD+
                        "Your Elo is "+ChatColor.LIGHT_PURPLE+IPlayer.getElo(ladder)+
                        ChatColor.GOLD+", searching in range "+ChatColor.BLUE+"["+queuePlayer.getRange().getMinKDR() +
                        " --> "+queuePlayer.getRange().getMaxKDR()+"]");
            }
            else{
                IPlayer.getPlayer().sendMessage(ChatColor.GOLD+"Waiting for another player to join the unranked queue");
            }
        }
    }

    /**
     * Removes a player from the queue
     * @param IPlayer the Player to remove
     * @param type The QueueType to remove from
     */
    public static void removeFromQueue(IPlayer IPlayer, QueueType type){
        if(contains(IPlayer.getName(), type)){
            IPlayer.getPlayer().sendMessage(ChatColor.RED+"You left the "+ChatColor.GOLD
                    +type.toString().toLowerCase()+ChatColor.RED+" match queue.");
            Iterator<QueueMember> it = queue.get(type).iterator();
            while(it.hasNext()){
                final QueueMember q = it.next();
                if(q.getPlayer().getName().equals(IPlayer.getName())){
                    queue.get(type).remove(q);
                    break;
                }
            }
        }
    }

    /**
     * If a player(username:name) is in the queue of said type
     * @param name The username
     * @param type The QueueType
     * @return True if contains, false if else
     */
    public static boolean contains(String name,QueueType type){
        if(queue.containsKey(type)){
            for(QueueMember q : queue.get(type)){
                if(q.getPlayer().getName().equals(name)){
                    return true;
                }
            }
        }
        if(type == QueueType.RANKED_PARTY){
            for(PartyQueueMember q : rankedPartyQueue){
                for(Player pl : q.getParty().getAllPlayers()){
                    if(pl.getName().equals(name)){
                        return true;
                    }
                }
            }
        }
        else if (type == QueueType.PARTY){
            for(PartyQueueMember q : partyQueue){
                for(Player pl : q.getParty().getAllPlayers()){
                    if(pl.getName().equals(name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static PartyQueueMember getPartyQueueMember(String name, QueueType type){
        if(type == QueueType.RANKED_PARTY){
            for(PartyQueueMember q : rankedPartyQueue){
                for(Player pl : q.getParty().getAllPlayers()){
                    if(pl.getName().equals(name)){
                        return q;
                    }
                }
            }
        }
        else if (type == QueueType.PARTY){
            for(PartyQueueMember q : partyQueue){
                for(Player pl : q.getParty().getAllPlayers()){
                    if(pl.getName().equals(name)){
                        return q;
                    }
                }
            }
        }
        return null;
    }

    public static QueueMember getQueueMember(String name, QueueType type){
        if(queue.containsKey(type)){
            for(QueueMember q : queue.get(type)){
                if(q.getPlayer().getName().equals(name)){
                    return q;
                }
            }
        }

        return null;
    }

    /**
     * If a player(username:name) is in any queue
     * @param name The username
     * @return True if contains, false is else
     */
    public static boolean inQueue(String name){
        return contains(name, QueueType.RANKED) || contains(name, QueueType.UNRANKED);
    }

    public static boolean inAnyQueue(IPlayer player){
        if(inQueue(player.getName())) return true;
        if(player.getParty() != null){
            if(inRankedPartyQueue(player.getParty()) || inPartyQueue(player.getParty())){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

}
