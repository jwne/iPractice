package com.shawckz.ipractice.match;

import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.player.IPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by 360 on 5/8/2015.
 */

/**
 * The DuelRequest class
 * Used to send a (/duel) duel request to another player.
 */
@AllArgsConstructor
public class DuelRequest {

    @Getter
    private Ladder ladder;
    @Getter
    private IPlayer sender;
    @Getter
    private IPlayer recipient;
    @Getter
    private long expiry = 0;

    /**
     * Send the request to the recipient
     */
    public void send(){
        Player p = Bukkit.getPlayerExact(sender.getName());
        Player t = Bukkit.getPlayerExact(recipient.getName());

        if(p != null && t != null){

            p.sendMessage(ChatColor.GOLD+"You sent a duel request to "+ChatColor.LIGHT_PURPLE+recipient.getName()+
                    ChatColor.GOLD+" with ladder "+ChatColor.AQUA+ladder.getName()+ChatColor.GOLD+
                    ".  This request times out in 15 seconds.");

            new FancyMessage(ChatColor.LIGHT_PURPLE+p.getName()+ChatColor.GOLD
                    +" has sent you a duel request with ladder "+ChatColor.AQUA+
                    ladder.getName()+ChatColor.GOLD+".")
                    .send(t);


            new FancyMessage("Type "+ChatColor.GREEN+"/accept "+p.getName()+ChatColor.GOLD+" or ")
                    .then(ChatColor.GREEN+""+ChatColor.BOLD+"[CLICK HERE]")
                    .command("/accept "+p.getName())
                    .tooltip(ChatColor.GOLD+"Accept "+p.getName()+"'s duel request")
                    .then(ChatColor.GOLD+" to accept.")
                    .send(t);

            t.sendMessage(ChatColor.GRAY+"This request will time out in 15 seconds.");

            expiry = System.currentTimeMillis() + (15 * 1000);
            recipient.getDuelRequests().add(this);
            sender.setDuelRequestCooldown(System.currentTimeMillis() + (15 * 1000));
        }

    }

}
