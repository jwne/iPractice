package com.shawckz.ipractice.match;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.arena.Arena;
import com.shawckz.ipractice.arena.ArenaType;
import com.shawckz.ipractice.arena.BasicArena;
import com.shawckz.ipractice.exception.PracticeException;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.handle.MatchHandler;
import com.shawckz.ipractice.match.handle.MatchManager;
import com.shawckz.ipractice.match.participant.MatchParticipant;
import com.shawckz.ipractice.match.participant.MatchPlayer;
import com.shawckz.ipractice.match.participant.MatchPlayerManager;
import com.shawckz.ipractice.match.team.PracticeTeam;
import com.shawckz.ipractice.match.team.Team;
import com.shawckz.ipractice.match.team.TeamManager;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.player.PlayerState;
import com.shawckz.ipractice.task.ArenaDupeTask;
import com.shawckz.ipractice.util.nametag.Nametag;
import com.shawckz.ipractice.util.nametag.NametagManager;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by 360 on 9/7/2015.
 */
@Getter
public class Match implements PracticeMatch {

    public static final Nametag TEAM = new Nametag("team", ChatColor.GREEN + "", "", true, true);
    public static final Nametag ENEMY = new Nametag("enemy", ChatColor.RED + "", "", true, true);
    public static final Nametag NORMAL = new Nametag("normal", ChatColor.YELLOW + "", "", true, true);
    private final String id;
    private final Ladder ladder;
    private final TeamManager teamManager;
    private final MatchPlayerManager playerManager;
    private final MatchHandler matchHandler;
    private final Map<String, String> inventories = new HashMap<>();
    @Setter private BasicArena arena = null;
    private boolean started = false;
    @Setter private boolean ranked = false;
    private int countdown = 5;
    private MatchManager matchManager;
    private boolean over = false;
    private int totalPlayers = 0;

    public Match(Ladder ladder) {
        this.id = UUID.randomUUID().toString();
        this.ladder = ladder;
        this.teamManager = new TeamManager(this);
        this.playerManager = new MatchPlayerManager(this);
        this.matchHandler = new MatchHandler(this);
    }

    public void startMatch(MatchManager matchManager) {
        this.matchManager = matchManager;
        teamManager.checkPerquisites();
        if (arena == null) {
            arena = (BasicArena) Practice.getArenaManager().getNextArena(ArenaType.NORMAL);
        }
        if (arena == null) {
            msg(ChatColor.RED + "There are currently no available arenas.  Please wait while we attempt to auto-generate one for you.");
            try {
                final Arena newest = Practice.getArenaManager().getNewestArena(ArenaType.NORMAL);
                if (newest != null) {
                    ArenaDupeTask task = new ArenaDupeTask(newest, 250, 0, 10, 250, 250) {
                        @Override
                        public void onComplete(Arena result) {
                            arena = (BasicArena) result;
                            Practice.getArenaManager().registerArena(result);
                        }
                    };
                    task.run();
                } else {
                    msg(ChatColor.RED + "Unable to start match(1): There are no available arenas.  Please contact an administrator and notify them of this error.");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                msg(ChatColor.RED + "Unable to start match(2): There are no available arenas.  Please contact an administrator and notify them of this error.");
                return;
            }
        }
        arena.setHasMatch(true);
        matchManager.registerMatch(this);
        matchHandler.register();

        String versus = "";
        for (PracticeTeam team : teamManager.getTeams().values()) {
            versus += ChatColor.AQUA + team.getName() + ChatColor.GOLD + " vs. ";
        }
        versus = versus.substring(0, versus.length() - 5);
        msg(versus);

        this.over = false;

        countdown = 5;
        for (MatchParticipant pmp : playerManager.getParticipants()) {
            for (MatchPlayer pmmp : pmp.getPlayers()) {
                IPlayer ip = pmmp.getPlayer();
                ip.equipKit(ladder);
                ip.setState(PlayerState.IN_MATCH);
                ip.getScoreboard().update();
                totalPlayers++;
            }
        }

        for (MatchParticipant pmp : playerManager.getParticipants()) {
            for (MatchPlayer pmmp : pmp.getPlayers()) {
                IPlayer ip = pmmp.getPlayer();
                ip.handlePlayerVisibility();
                if (pmp.getTeam().getSpawn() == Team.ALPHA) {
                    ip.getPlayer().teleport(arena.getSpawnAlpha());
                } else if (pmp.getTeam().getSpawn() == Team.BRAVO) {
                    ip.getPlayer().teleport(arena.getSpawnBravo());
                } else {
                    throw new PracticeException("Unknown Team enumeration: " + pmp.getTeam().getSpawn().toString());
                }
                for (Player pl : playerManager.getAllPlayers()) {
                    for (Player pl2 : playerManager.getAllPlayers()) {
                        pl.showPlayer(pl2);
                        pl2.showPlayer(pl);
                    }
                }
                NametagManager.getPlayer(pmmp.getPlayer().getPlayer()).reset();
                for (MatchParticipant tpmp : playerManager.getParticipants()) {
                    for (MatchPlayer tmp : tpmp.getPlayers()) {
                        NametagManager.getPlayer(tmp.getPlayer().getPlayer()).reset();
                    }
                }
            }
        }

        for(MatchParticipant pmp : playerManager.getParticipants()){
            for(MatchPlayer pmmp : pmp.getPlayers()){
                PracticeTeam team = teamManager.getTeam(pmmp.getPlayer());
                for (MatchParticipant tpmp : playerManager.getParticipants()) {
                    for (MatchPlayer tmp : tpmp.getPlayers()) {
                        NametagManager.getPlayer(tmp.getPlayer().getPlayer()).reset();
                        if (teamManager.getTeam(tmp.getPlayer()).getName().equals(team.getName())) {
                            NametagManager.getPlayer(pmmp.getPlayer().getPlayer()).setPlayerNametag(NametagManager.getPlayer(tmp.getPlayer().getPlayer()), TEAM);
                            NametagManager.getPlayer(tmp.getPlayer().getPlayer()).setPlayerNametag(NametagManager.getPlayer(pmmp.getPlayer().getPlayer()), TEAM);
                        } else {
                            NametagManager.getPlayer(pmmp.getPlayer().getPlayer()).setPlayerNametag(NametagManager.getPlayer(tmp.getPlayer().getPlayer()), ENEMY);
                            NametagManager.getPlayer(tmp.getPlayer().getPlayer()).setPlayerNametag(NametagManager.getPlayer(pmmp.getPlayer().getPlayer()), ENEMY);
                        }
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (over) {
                    cancel();
                    return;
                }
                if (countdown > 0) {
                    msg(ChatColor.GOLD + "Starting in " + ChatColor.BLUE + countdown + ChatColor.GOLD + "...");
                    countdown--;
                } else {
                    countdown = 0;
                    started = true;
                    over = false;
                    msg(ChatColor.GREEN + "Match started.");
                    for (MatchParticipant pmp : playerManager.getParticipants()) {
                        for (MatchPlayer pmmp : pmp.getPlayers()) {
                            IPlayer ip = pmmp.getPlayer();
                            ip.getScoreboard().update();
                        }
                    }
                    for (Player pl : playerManager.getAllPlayers()) {
                        for (Player pl2 : playerManager.getAllPlayers()) {
                            pl.showPlayer(pl2);
                            pl2.showPlayer(pl);
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getPlugin(), 20L, 20L);

    }

    @Override
    public void endMatch() {
        this.over = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                arena.setHasMatch(false);
                matchHandler.unregister();
                for (MatchParticipant pl : playerManager.getParticipants()) {
                    for (MatchPlayer p : pl.getPlayers()) {
                        if (p.isAlive()) {
                            if (ranked) {
                                p.getPlayer().incrementWins(ladder);
                            }
                            p.getPlayer().sendToSpawn();
                            continue;
                        } else {
                            if (ranked) {
                                p.getPlayer().incrementLosses(ladder);
                            }
                            if (!started) {
                                p.getPlayer().sendToSpawn();
                                continue;
                            }
                        }
                        if (p.isSpectating()) {
                            p.getPlayer().sendToSpawn();
                        }
                        p.setSpectating(false);
                        p.setAlive(false);
                    }
                }
            }
        }.runTaskLater(Practice.getPlugin(), 100L);
        matchManager.unregisterMatch(this);
    }

    public void eliminatePlayer(final IPlayer player, IPlayer killer) {
        MatchParticipant participant = playerManager.getParticipant(player);
        if (participant != null) {
            playerManager.getPlayer(player).setAlive(false);

            if (killer != null) {
                for (Player pl : getPlayers()) {
                    IPlayer ip = Practice.getCache().getIPlayer(pl);
                    pl.sendMessage(getRelationalColor(ip, player) + player.getName() + ChatColor.GOLD + " was killed by " + getRelationalColor(ip, killer) + killer.getName() + ChatColor.GOLD + ".");
                }

            } else {
                for (Player pl : getPlayers()) {
                    IPlayer ip = Practice.getCache().getIPlayer(pl);
                    pl.sendMessage(getRelationalColor(ip, player) + player.getName() + ChatColor.GOLD + " was killed.");
                }
            }

            inventories.put(player.getName(), new MatchInventory(player.getPlayer()).getUuid());

            final Match m = this;
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.makeSpectator(m);
                    playerManager.getPlayer(player).setSpectating(true);
                }
            }.runTaskLater(Practice.getPlugin(), 5L);

            if (ranked) {
                player.getDeaths().put(ladder, (player.getDeaths().get(ladder) + 1));
                if (killer != null) {
                    killer.getKills().put(ladder, (player.getKills().get(ladder) + 1));
                }
            }


            boolean shouldEliminate = true;
            PracticeTeam team = participant.getTeam();

            for (MatchParticipant pl : playerManager.getParticipants()) {
                if (pl.getTeam().getName().equals(team.getName())) {
                    for (MatchPlayer pla : pl.getPlayers()) {
                        if (pla.isAlive()) {
                            shouldEliminate = false;
                            break;
                        }
                    }
                }
            }
            if (shouldEliminate) {
                team.setEliminated(true);
            }

            int remainingTeams = 0;
            for (PracticeTeam t : teamManager.getTeams().values()) {
                if (!t.isEliminated()) {
                    remainingTeams++;
                }
            }

            if (remainingTeams <= 1) {
                //Only one team is left, that team wins
                for (PracticeTeam t : teamManager.getTeams().values()) {
                    if (!t.isEliminated()) {
                        handleWin(t);
                        break;
                    }
                }
            }

        } else {
            throw new PracticeException("Can not eliminate null player");
        }
    }

    public void handleWin(final PracticeTeam team) {
        msg(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        msg(ChatColor.BLUE + "Match Results");
        msg(ChatColor.GOLD + "Winner(s): " + ChatColor.LIGHT_PURPLE + team.getName());

        if (ranked) {
            int winnerElo = 0;
            int wx = 0;
            int loserElo = 0;
            int lx = 0;

            Map<String, Integer> ogElo = new HashMap<>();
            Map<String, Integer> newElo = new HashMap<>();

            for (MatchParticipant pl : playerManager.getParticipants()) {
                for (MatchPlayer p : pl.getPlayers()) {
                    if (teamManager.getTeam(p.getPlayer()).getName().equals(team.getName())) {
                        winnerElo += p.getPlayer().getElo(ladder);
                        wx++;
                    } else {
                        loserElo += p.getPlayer().getElo(ladder);
                        lx++;
                    }
                    ogElo.put(p.getPlayer().getName(), p.getPlayer().getElo(ladder));
                }
            }

            winnerElo /= wx;
            loserElo /= lx;

            for (MatchParticipant pl : playerManager.getParticipants()) {
                for (MatchPlayer p : pl.getPlayers()) {
                    if (teamManager.getTeam(p.getPlayer()).getName().equals(team.getName())) {
                        p.getPlayer().updateElo(ladder, loserElo, true);
                    } else {
                        p.getPlayer().updateElo(ladder, winnerElo, false);
                    }
                    newElo.put(p.getPlayer().getName(), p.getPlayer().getElo(ladder));
                }
            }

            sendEloChanges(ogElo, newElo);
        }

        for (MatchParticipant pl : playerManager.getParticipants()) {
            if (pl.getTeam().getName().equals(team.getName())) {
                for (MatchPlayer mp : pl.getPlayers()) {
                    if (mp.isAlive()) {
                        inventories.put(mp.getPlayer().getName(), new MatchInventory(mp.getPlayer().getPlayer()).getUuid());
                        mp.getPlayer().makeSpectator(this);
                        playerManager.getPlayer(mp.getPlayer()).setSpectating(true);
                    }
                }
            }
        }

        msg(ChatColor.GRAY + " ");
        msg(ChatColor.BLUE + "Inventories " + ChatColor.GRAY + "(Click to view)");
        for (MatchParticipant pl : playerManager.getParticipants()) {
            for (MatchPlayer mp : pl.getPlayers()) {

                PracticeTeam mpTeam = teamManager.getTeam(mp.getPlayer());
                FancyMessage t = new FancyMessage(ChatColor.GREEN + "Your Team: ");
                FancyMessage e = new FancyMessage(ChatColor.RED + "Enemy Team: ");
                for (MatchParticipant tpl : playerManager.getParticipants()) {
                    for (MatchPlayer tmp : tpl.getPlayers()) {
                        if (teamManager.getTeam(tmp.getPlayer()).getName().equals(mpTeam.getName())) {
                            t.then(ChatColor.WHITE + tmp.getPlayer().getName() + " ")
                                    .tooltip(ChatColor.GRAY + "Click to view " + tmp.getPlayer().getName() + "'s Inventory")
                                    .command("/viewinv " + inventories.get(tmp.getPlayer().getName()));
                        } else {
                            e.then(ChatColor.WHITE + tmp.getPlayer().getName() + " ")
                                    .tooltip(ChatColor.GRAY + "Click to view " + tmp.getPlayer().getName() + "'s Inventory")
                                    .command("/viewinv " + inventories.get(tmp.getPlayer().getName()));
                        }
                    }
                }
                t.send(mp.getPlayer().getPlayer());
                e.send(mp.getPlayer().getPlayer());
            }
        }

        msg(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        endMatch();
    }

    private void sendEloChanges(Map<String, Integer> before, Map<String, Integer> after) {
        for (MatchPlayer mp : playerManager.getMatchPlayers()) {
            String s = "";
            IPlayer ip = mp.getPlayer();
            for (String k : before.keySet()) {
                IPlayer kip = Practice.getCache().getIPlayer(k);
                int difference = after.get(k) - before.get(k);
                int elo = after.get(k);
                s += getRelationalColor(ip, kip) + k + ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + elo +
                        ChatColor.YELLOW + "(" + (difference >= 0 ? ChatColor.GREEN + "+" +
                        difference : ChatColor.RED + "" + difference)
                        + ChatColor.YELLOW + ")" + ChatColor.DARK_GRAY + "] ";
                //Player1[1010(+10)]
                //Player2[990(-10)]
            }
            ip.getPlayer().sendMessage(ChatColor.GOLD + "Elo Changes: " + s);
        }
    }

    public void msg(String msg) {
        for (MatchParticipant pmp : playerManager.getParticipants()) {
            for (MatchPlayer pl : pmp.getPlayers()) {
                pl.getPlayer().getPlayer().sendMessage(msg);
            }
        }
    }

    public ChatColor getRelationalColor(IPlayer forWho, IPlayer target) {
        if (teamManager.getTeam(forWho).getName().equals(teamManager.getTeam(target).getName())) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.RED;
        }
    }

    public int getRemainingPlayers(){
        int i = 0;
        for (MatchParticipant pmp : playerManager.getParticipants()) {
            for (MatchPlayer pl : pmp.getPlayers()) {
                if(pl.isAlive()){
                    i++;
                }
            }
        }
        return i;
    }

    @Override
    public Set<Player> getPlayers() {
        return playerManager.getAllPlayers();
    }

    @Override
    public MatchType getType() {
        return MatchType.NORMAL;
    }

    @Override
    public String getOpponent(IPlayer player) {
        PracticeTeam team = teamManager.getTeam(player);
        String s = "";
        for (MatchParticipant pmp : playerManager.getParticipants()) {
            for (MatchPlayer pl : pmp.getPlayers()) {
                PracticeTeam plTeam = teamManager.getTeam(pl.getPlayer());
                if (!plTeam.getName().equals(team.getName())) {
                    s += plTeam.getName() + ", ";
                }
            }
        }
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 2);
        }
        return s;
    }
}
