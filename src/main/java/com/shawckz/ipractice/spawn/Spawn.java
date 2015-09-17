package com.shawckz.ipractice.spawn;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.kit.KitBuilder;
import com.shawckz.ipractice.kite.KiteSelect;
import com.shawckz.ipractice.match.*;
import com.shawckz.ipractice.party.PartiesInv;
import com.shawckz.ipractice.party.PartyEvent;
import com.shawckz.ipractice.party.PartyEventSelect;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.queue.Queue;
import com.shawckz.ipractice.queue.QueueSelect;
import com.shawckz.ipractice.queue.QueueType;
import com.shawckz.ipractice.spawn.item.SpawnItem;
import com.shawckz.ipractice.spawn.item.SpawnItemAction;
import com.shawckz.ipractice.spawn.item.SpawnItemType;
import com.shawckz.ipractice.spawn.items.SimpleSpawnItem;
import com.shawckz.ipractice.util.ItemBuilder;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

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
                .name(ChatColor.GOLD+"Join a Queue"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(!Practice.getQueueManager().inQueue(player)){
                    new QueueSelect(player){
                        @Override
                        public void onSelect(final QueueType type) {
                            new LadderSelect(player, type){
                                @Override
                                public void onSelect(final Ladder ladder) {
                                    Queue queue = Practice.getQueueManager().getQueues().get(type);
                                    if(queue != null){
                                        if(queue.canJoin(player)){
                                            queue.addToQueue(player, ladder);
                                            player.getPlayer().sendMessage(ChatColor.BLUE + "You joined the " + ChatColor.GREEN +
                                                    WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_"," "))
                                                    + ChatColor.BLUE + " queue.");
                                            player.getPlayer().getInventory().clear();
                                            player.getPlayer().getInventory().setArmorContents(null);
                                            player.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED+"Leave the queue").build());
                                            player.getPlayer().updateInventory();
                                            player.getScoreboard().update();
                                        }
                                        else{
                                            player.getPlayer().sendMessage(ChatColor.RED+"You can't join the queue right now.");
                                        }
                                    }
                                    else{
                                        player.getPlayer().sendMessage(ChatColor.RED+"That queue is not yet supported.");
                                    }
                                }
                            };
                        }
                    };
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.RED+"You are already in a queue!");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(4, new ItemBuilder(Material.EYE_OF_ENDER)
                .name(ChatColor.GOLD + "Host an Event"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().sendMessage(ChatColor.GOLD + "The Event System is currently in development and will be out soon!");
            }
        }));

        registerItem(new SimpleSpawnItem(6, new ItemBuilder(Material.FIREWORK_CHARGE)
                .name(ChatColor.GOLD + "Kite Practice"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                new KiteSelect(player.getPlayer());
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
                .name(ChatColor.GOLD+"Join a Party Queue"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(player.getParty() != null){
                    if(player.getParty().getLeader().equals(player.getName())){
                        if(!Practice.getQueueManager().inQueue(player)){
                            new QueueSelect(player){
                                @Override
                                public void onSelect(final QueueType type) {
                                    new LadderSelect(player){
                                        @Override
                                        public void onSelect(Ladder ladder) {
                                            Queue queue = Practice.getQueueManager().getQueues().get(type);
                                            Set<IPlayer> players = new HashSet<IPlayer>();
                                            for(Player pl : player.getParty().getAllPlayers()){
                                                players.add(Practice.getCache().getIPlayer(pl));
                                            }
                                            queue.addToQueue(players, ladder);
                                            player.getParty().msg(ChatColor.AQUA + "" + ChatColor.BOLD + "(PARTY) " + ChatColor.RESET + "" +
                                                    ChatColor.BLUE + "Your party joined the " + ChatColor.GREEN +
                                                    WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                    + ChatColor.BLUE + " queue.");
                                            player.getPlayer().getInventory().clear();
                                            player.getPlayer().getInventory().setArmorContents(null);
                                            player.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED+"Leave the queue").build());
                                            player.getPlayer().updateInventory();
                                            player.getScoreboard().update();
                                        }
                                    };
                                }
                            };
                        }
                        else{
                            player.getPlayer().sendMessage(ChatColor.RED+"You are already in a queue.");
                        }
                    }
                    else{
                        player.getPlayer().sendMessage(ChatColor.RED+"Only the party leader can do this.");
                    }
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.RED+"You are not in a party.");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(4, new ItemBuilder(new ItemStack(Material.FIREWORK_CHARGE))
                .name(ChatColor.GOLD+"Party Kite Practice"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().sendMessage(ChatColor.GOLD + "Party Kite Practice is currently in development and will be out soon!");
            }
        }));

        registerItem(new SimpleSpawnItem(5, new ItemBuilder(new ItemStack(Material.ENDER_CHEST))
                .name(ChatColor.GOLD+"Parties to Duel"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                PartiesInv.open(player.getPlayer());
            }
        }));

        registerItem(new SimpleSpawnItem(6, new ItemBuilder(new ItemStack(Material.REDSTONE_TORCH_ON))
                .name(ChatColor.GOLD+"Party Events"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(player.getParty() != null){
                    if(player.getParty().getLeader().equals(player.getName())){
                        new PartyEventSelect(player.getPlayer()){
                            @Override
                            public void onSelect(final PartyEvent event) {
                                new LadderSelect(player){
                                    @Override
                                    public void onSelect(Ladder ladder) {
                                        MatchBuilder mb = Practice.getMatchManager().matchBuilder(ladder);
                                        if(event == PartyEvent.FFA){
                                            int x = 0;
                                            for(Player pl : player.getParty().getAllPlayers()){
                                                if(x % 2 == 0){
                                                    mb.registerTeam(new PracticeTeam(pl.getName(), Team.ALPHA));
                                                }
                                                else{
                                                    mb.registerTeam(new PracticeTeam(pl.getName(), Team.BRAVO));
                                                }
                                                mb.withPlayer(pl, pl.getName());
                                                x++;
                                            }
                                            mb.build().startMatch(Practice.getMatchManager());
                                        }
                                        else if (event == PartyEvent.TWO_TEAMS){
                                            
                                        }
                                        else{
                                            player.getPlayer().sendMessage(ChatColor.RED+"That party event is not yet supported.");
                                        }
                                    }
                                };
                            }
                        };
                    }
                    else{
                        player.getPlayer().sendMessage(ChatColor.RED+"Only the party leader can do this.");
                    }
                }
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
            if(Practice.getQueueManager().inQueue(iPlayer)){
                Practice.getQueueManager().removeFromQueue(iPlayer);
                iPlayer.sendToSpawnNoTp();
                e.setCancelled(true);
            }
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
