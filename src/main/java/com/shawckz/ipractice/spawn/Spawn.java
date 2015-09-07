package com.shawckz.ipractice.spawn;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.kit.KitBuilder;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.match.LadderSelect;
import com.shawckz.ipractice.party.PartiesInv;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueType;
import com.shawckz.ipractice.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Spawn implements Listener {

    public Spawn(Practice instance) {

        Bukkit.getServer().getPluginManager().registerEvents(this, instance);

        //Normal Items
        registerItem(new SimpleSpawnItem(0, new ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.GOLD + "Kit Editor"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                new LadderSelect(player) {
                    @Override
                    public void onSelect(Ladder ladder) {
                        if (ladder != null) {
                            if (ladder.isEditable()) {
                                new KitBuilder(ladder, player.getPlayer()).init();
                            } else {
                                player.getPlayer().sendMessage(ChatColor.RED + "You cannot edit the "
                                        + ChatColor.GOLD + ladder.getName() + ChatColor.RED + " ladder.");
                            }
                        }
                    }
                };
            }
        }));

        registerItem(new SimpleSpawnItem(2, new ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.GOLD+"Ranked Queue"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("rmatch");
            }
        }));

        registerItem(new SimpleSpawnItem(3, new ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.GOLD + "Unranked Queue"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("umatch");
            }
        }));

        registerItem(new SimpleSpawnItem(5, new ItemBuilder(Material.EYE_OF_ENDER)
                .name(ChatColor.GOLD + "Host an PracticeEvent"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().sendMessage(ChatColor.GOLD + "Coming soon!");
            }
        }));


        registerItem(new SimpleSpawnItem(8, new ItemBuilder(new ItemStack(Material.INK_SACK,1, DyeColor.LIME.getDyeData()))
                .name(ChatColor.GOLD+"Create a Party"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("party create");
            }
        }));

        //Party items

        registerItem(new SimpleSpawnItem(0, new ItemBuilder(new ItemStack(Material.NETHER_STAR))
                .name(ChatColor.GOLD+"Party Members"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(player.getParty() != null){
                    player.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE+player.getParty().getLeader()+
                            ChatColor.GOLD+"'s Party");
                    for(Player p : player.getParty().getAllPlayers()){
                        player.getPlayer().sendMessage(ChatColor.GRAY+" - "+ChatColor.GOLD+p.getName());
                    }
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.RED+"You are not in a party.");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(2, new ItemBuilder(new ItemStack(Material.DIAMOND_SWORD))
                .name(ChatColor.GOLD+"Ranked 2v2 Party Queue"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("prmatch");
            }
        }));
        registerItem(new SimpleSpawnItem(3, new ItemBuilder(new ItemStack(Material.IRON_SWORD))
                .name(ChatColor.GOLD+"Unranked Party Queue"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("pmatch");
            }
        }));

        registerItem(new SimpleSpawnItem(5, new ItemBuilder(new ItemStack(Material.ENDER_CHEST))
                .name(ChatColor.GOLD+"Parties to Duel"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                PartiesInv.open(player.getPlayer());
            }
        }));

        registerItem(new SimpleSpawnItem(8, new ItemBuilder(new ItemStack(Material.FIREBALL))
                .name(ChatColor.GOLD+"Leave the Party"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("party leave");
            }
        }));

    }

    private final Set<SpawnItem> items = new HashSet<>();

    public void registerItem(SpawnItem item){
        items.add(item);
    }

    public void giveItems(IPlayer player){
        if(player.getParty() != null){
            giveItems(player, SpawnItemType.PARTY);
        }
        else{
            giveItems(player, SpawnItemType.NORMAL);
        }
    }

    public void giveItems(IPlayer player, SpawnItemType type){
        for(SpawnItem i : items){
            if(i.getType() == type){
                i.give(player.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        final Player p = e.getPlayer();
        final IPlayer iPlayer = Practice.getCache().getIPlayer(p);

        if(iPlayer.getState() != PlayerState.AT_SPAWN) return;

        if(p.getItemInHand() != null && p.getItemInHand().getType() == Material.BLAZE_POWDER && p.getItemInHand().hasItemMeta()
                &&p.getItemInHand().getItemMeta().getDisplayName() != null){
            if(Queue.contains(p.getName(), QueueType.UNRANKED)){
                Queue.removeFromQueue(iPlayer,QueueType.UNRANKED);
            }
            if (Queue.contains(p.getName(), QueueType.RANKED)) {
                Queue.removeFromQueue(iPlayer, QueueType.RANKED);
            }
            if(iPlayer.getParty() != null){
                if(Queue.inPartyQueue(iPlayer.getParty())){
                    Queue.removeFromPartyQueue(iPlayer.getParty());
                }
                if(Queue.inRankedPartyQueue(iPlayer.getParty())){
                    Queue.removeFromRankedPartyQueue(iPlayer.getParty());
                }
            }
            iPlayer.sendToSpawn();
            e.setCancelled(true);
            return;
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){

            for(SpawnItem item : items){
                if(item.getItem().equals(p.getItemInHand())){
                    if(item.getType() == SpawnItemType.NORMAL && iPlayer.getParty() == null){
                        e.setCancelled(true);
                        item.getAction().onClick(iPlayer);
                        break;
                    }
                    else if (item.getType() == SpawnItemType.PARTY && iPlayer.getParty() != null){
                        e.setCancelled(true);
                        item.getAction().onClick(iPlayer);
                        break;
                    }
                }
            }

        }
    }

}
