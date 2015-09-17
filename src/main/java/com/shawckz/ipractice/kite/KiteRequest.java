package com.shawckz.ipractice.kite;

import com.shawckz.ipractice.player.IPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by 360 on 5/8/2015.
 */

@RequiredArgsConstructor
@Getter
public class KiteRequest {
    private final IPlayer sender;
    private final IPlayer recipient;
    private final Player runner;
    private final Player chaser;
    private long expiry = 0;

    public void send(){
        sender.getPlayer().sendMessage(ChatColor.GOLD + "You sent a kite duel request to " + ChatColor.LIGHT_PURPLE + recipient.getName() + ChatColor.GOLD + ".");

        new FancyMessage(ChatColor.LIGHT_PURPLE+sender.getName()+ChatColor.GOLD
                +" has sent you a kite duel request.  ")
                .then(ChatColor.GOLD+"Runner: "+ChatColor.LIGHT_PURPLE+runner.getName()+" ")
                .then(ChatColor.GOLD+"Chaser: "+ChatColor.LIGHT_PURPLE+chaser.getName())
                .send(recipient.getPlayer());

        new FancyMessage(ChatColor.GREEN+""+ChatColor.BOLD+"[CLICK HERE]")
                .command("/accept "+sender.getName())
                .tooltip(ChatColor.GOLD+"Accept "+sender.getName()+"'s duel request")
                .then(ChatColor.GOLD + " to accept.")
                .send(recipient.getPlayer());
        recipient.getPlayer().sendMessage(ChatColor.GRAY+"This request will time out in 15 seconds.");

        expiry = System.currentTimeMillis() + (15 * 1000);
        recipient.getKiteRequests().add(this);
    }

}
