package com.shawckz.ipractice.spawn;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.ArenaType;
import com.shawckz.ipractice.kit.KitBuilder;
import com.shawckz.ipractice.kite.KiteSelect;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.ladder.LadderSelect;
import com.shawckz.ipractice.match.*;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
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
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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
        registerItem(new SimpleSpawnItem(0, new ItemBuilder(Material.BOOK)
                .name(ChatColor.BLUE + "Kit Editor"), new SpawnItemAction() {
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
                .name(ChatColor.BLUE + "Join a Queue"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (!Practice.getQueueManager().inQueue(player)) {
                    new QueueSelect(player) {
                        @Override
                        public void onSelect(final QueueType type) {
                            new LadderSelect(player, type) {
                                @Override
                                public void onSelect(final Ladder ladder) {
                                    Queue queue = Practice.getQueueManager().getQueues().get(type);
                                    if (queue != null) {
                                        if (queue.canJoin(player)) {
                                            queue.addToQueue(player, ladder);
                                            if(queue.getType() == QueueType.RANKED){
                                                player.getPlayer().sendMessage(ChatColor.BLUE + "You joined the " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue with "+
                                                        ChatColor.GOLD+player.getElo(ladder)+" ELO"+ ChatColor.BLUE+".");
                                            }
                                            else if (queue.getType() == QueueType.PING){
                                                player.getPlayer().sendMessage(ChatColor.BLUE + "You joined the " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue with "+
                                                        ChatColor.GOLD+((CraftPlayer)player.getPlayer()).getHandle().ping+" ping"+ ChatColor.BLUE+".");
                                            }
                                            else{
                                                player.getPlayer().sendMessage(ChatColor.BLUE + "You joined the " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue.");
                                            }
                                            player.getPlayer().getInventory().clear();
                                            player.getPlayer().getInventory().setArmorContents(null);
                                            player.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Leave the queue").build());
                                            player.getPlayer().updateInventory();
                                            player.getScoreboard().update();
                                        } else {
                                            player.getPlayer().sendMessage(ChatColor.RED + "You can't join the queue right now.");
                                        }
                                    } else {
                                        player.getPlayer().sendMessage(ChatColor.RED + "That queue is not yet supported.");
                                    }
                                }
                            };
                        }
                    };
                } else {
                    player.getPlayer().sendMessage(ChatColor.RED + "You are already in a queue!");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(4, new ItemBuilder(Material.EYE_OF_ENDER)
                .name(ChatColor.BLUE + "Host an Event"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(player.getPlayer().hasPermission("practice.events.host")){
                    player.getPlayer().sendMessage(ChatColor.GOLD+"Events will be available for donators to host very soon!");
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.GOLD+"Only "+ChatColor.LIGHT_PURPLE+"donators"+ChatColor.GOLD+" can host events!");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(6, new ItemBuilder(Material.FIREWORK_CHARGE)
                .name(ChatColor.BLUE + "Kite Practice"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(Practice.getArenaManager().getNewestArena(ArenaType.KITE) == null){
                    player.getPlayer().sendMessage(ChatColor.RED+"Kite Practice does not have any arenas currently setup!");
                    return;
                }
                new KiteSelect(player.getPlayer());
            }
        }));


        registerItem(new SimpleSpawnItem(8, new ItemBuilder(new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData()))
                .name(ChatColor.BLUE + "Create a Party"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("party create");
            }
        }));

        //Party items

        registerItem(new SimpleSpawnItem(0, new ItemBuilder(new ItemStack(Material.NETHER_STAR))
                .name(ChatColor.BLUE + "Party Members"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (player.getParty() != null) {
                    player.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + player.getParty().getLeader() +
                            ChatColor.GOLD + "'s Party");
                    for (Player p : player.getParty().getAllPlayers()) {
                        player.getPlayer().sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + p.getName());
                    }
                } else {
                    player.getPlayer().sendMessage(ChatColor.RED + "You are not in a party.");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(2, new ItemBuilder(new ItemStack(Material.DIAMOND_SWORD))
                .name(ChatColor.BLUE + "Join a Party Queue"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (player.getParty() != null) {
                    if (player.getParty().getLeader().equals(player.getName())) {
                        if (!Practice.getQueueManager().inQueue(player)) {
                            if (player.getParty().getAllPlayers().size() == 2) {
                                new QueueSelect(player) {
                                    @Override
                                    public void onSelect(final QueueType type) {
                                        new LadderSelect(player) {
                                            @Override
                                            public void onSelect(Ladder ladder) {
                                                Queue queue = Practice.getQueueManager().getQueues().get(type);
                                                queue.addToQueue(player, ladder);
                                                player.getParty().msg(ChatColor.AQUA + "" + ChatColor.BOLD + "(PARTY) " + ChatColor.RESET + "" +
                                                        ChatColor.BLUE + "Your party joined the " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue.");
                                                player.getPlayer().getInventory().clear();
                                                player.getPlayer().getInventory().setArmorContents(null);
                                                player.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Leave the queue").build());
                                                player.getPlayer().updateInventory();
                                                player.getScoreboard().update();
                                            }
                                        };
                                    }
                                };
                            }
                            else{
                                player.getPlayer().sendMessage(ChatColor.RED+"Your party must have 2 players to do this.");
                            }
                        } else {
                            player.getPlayer().sendMessage(ChatColor.RED + "You are already in a queue.");
                        }
                    } else {
                        player.getPlayer().sendMessage(ChatColor.RED + "Only the party leader can do this.");
                    }
                } else {
                    player.getPlayer().sendMessage(ChatColor.RED + "You are not in a party.");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(4, new ItemBuilder(new ItemStack(Material.FIREWORK_CHARGE))
                .name(ChatColor.BLUE + "Party Kite Practice"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().sendMessage(ChatColor.GOLD + "Party Kite Practice is currently in development and will be out soon!");
            }
        }));

        registerItem(new SimpleSpawnItem(5, new ItemBuilder(new ItemStack(Material.ENDER_CHEST))
                .name(ChatColor.BLUE + "Parties to Duel"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                PartiesInv.open(player.getPlayer());
            }
        }));

        registerItem(new SimpleSpawnItem(6, new ItemBuilder(new ItemStack(Material.REDSTONE_TORCH_ON))
                .name(ChatColor.BLUE + "Party Events"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (player.getParty() != null) {
                    if (player.getParty().getLeader().equals(player.getName())) {
                        if(player.getParty().getAllPlayers().size() >= 2) {
                            new PartyEventSelect(player.getPlayer()) {
                                @Override
                                public void onSelect(final PartyEvent event) {
                                    new LadderSelect(player) {
                                        @Override
                                        public void onSelect(Ladder ladder) {
                                            MatchBuilder mb = Practice.getMatchManager().matchBuilder(ladder);
                                            if (event == PartyEvent.FFA) {
                                                int x = 0;
                                                for (Player pl : player.getParty().getAllPlayers()) {
                                                    if (x % 2 == 0) {
                                                        mb.registerTeam(new PracticeTeam(pl.getName(), Team.ALPHA));
                                                    } else {
                                                        mb.registerTeam(new PracticeTeam(pl.getName(), Team.BRAVO));
                                                    }
                                                    mb.withPlayer(pl, pl.getName());
                                                    x++;
                                                }
                                                mb.build().startMatch(Practice.getMatchManager());
                                            } else if (event == PartyEvent.TWO_TEAMS) {
                                                mb.registerTeam(new PracticeTeam("Team A", Team.ALPHA));
                                                mb.registerTeam(new PracticeTeam("Team B", Team.BRAVO));
                                                int x = 0;
                                                for(Player pl : player.getParty().getAllPlayers()){
                                                    if(x % 2 == 0){
                                                        mb.withPlayer(pl, "Team A");
                                                        pl.sendMessage(ChatColor.GOLD + "You are on " + ChatColor.AQUA + "Team A");
                                                    }
                                                    else{
                                                        mb.withPlayer(pl, "Team B");
                                                        pl.sendMessage(ChatColor.GOLD+"You are on "+ChatColor.AQUA+"Team B");
                                                    }
                                                    x++;
                                                }
                                                mb.build().startMatch(Practice.getMatchManager());
                                            } else {
                                                player.getPlayer().sendMessage(ChatColor.RED + "That party event is not yet supported.");
                                            }
                                        }
                                    };
                                }
                            };
                        }
                        else{
                            player.getPlayer().sendMessage(ChatColor.RED+"You must have at least 2 players in your party to do this.");
                        }
                    } else {
                        player.getPlayer().sendMessage(ChatColor.RED + "Only the party leader can do this.");
                    }
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.RED+"You are not in a party.");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(8, new ItemBuilder(new ItemStack(Material.FIREBALL))
                .name(ChatColor.BLUE + "Leave the Party"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("party leave");
            }
        }));

        registerItem(new SimpleSpawnItem(8, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Back to Spawn")
                , SpawnItemType.SPECTATOR, new SpawnItemAction() {
            @Override
            public void onClick(IPlayer player) {
                player.setState(PlayerState.AT_SPAWN);
                player.sendToSpawn();
            }
        }));

    }

    private final Set<SpawnItem> items = new HashSet<>();

    public void registerItem(SpawnItem item) {
        items.add(item);
    }

    public void giveItems(IPlayer player) {
        if (player.getState() == PlayerState.SPECTATING_MATCH) {
            giveItems(player, SpawnItemType.SPECTATOR);
        }
        else if (player.getParty() != null) {
            giveItems(player, SpawnItemType.PARTY);
        }
        else{
            giveItems(player, SpawnItemType.NORMAL);
        }
    }

    public void giveItems(IPlayer player, SpawnItemType type) {
        for (SpawnItem i : items) {
            if (i.getType() == type) {
                i.give(player.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final IPlayer iPlayer = Practice.getCache().getIPlayer(p);

        if (iPlayer.getState() != PlayerState.AT_SPAWN) return;

        if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.BLAZE_POWDER && p.getItemInHand().hasItemMeta()
                && p.getItemInHand().getItemMeta().getDisplayName() != null) {
            if (Practice.getQueueManager().inQueue(iPlayer)) {
                Practice.getQueueManager().removeFromQueue(iPlayer);
                iPlayer.sendToSpawnNoTp();
                e.setCancelled(true);
            }
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {

            for (SpawnItem item : items) {
                if (item.getItem().equals(p.getItemInHand())) {
                    if (item.getType() == SpawnItemType.NORMAL && iPlayer.getParty() == null) {
                        e.setCancelled(true);
                        item.getAction().onClick(iPlayer);
                        break;
                    } else if (item.getType() == SpawnItemType.PARTY && iPlayer.getParty() != null) {
                        e.setCancelled(true);
                        item.getAction().onClick(iPlayer);
                        break;
                    }
                }
            }

        }
    }

}
