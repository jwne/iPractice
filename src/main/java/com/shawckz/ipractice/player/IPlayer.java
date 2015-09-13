package com.shawckz.ipractice.player;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.database.mongo.annotations.CollectionName;
import com.shawckz.ipractice.database.mongo.annotations.DatabaseSerializer;
import com.shawckz.ipractice.database.mongo.annotations.MongoColumn;
import com.shawckz.ipractice.kit.Kit;
import com.shawckz.ipractice.kit.KitBuilder;
import com.shawckz.ipractice.kit.KitHandler;
import com.shawckz.ipractice.match.DuelRequest;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.cache.CachePlayer;
import com.shawckz.ipractice.rating.Elo;
import com.shawckz.ipractice.scoreboard.practice.PracticeScoreboard;
import com.shawckz.ipractice.serial.LadderIntegerSerializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CollectionName(name = "iplayers")
public class IPlayer extends CachePlayer {

    public static final int DEFAULT_ELO = 1000;

    public IPlayer() {
    }

    @MongoColumn(name = "username")
    @NonNull @Getter @Setter private String name;

    @MongoColumn(name = "uniqueId", identifier = true)
    @NonNull @Getter private String uniqueId;//i would finalize but automongo can't populate then :(


    @Getter @Setter private Player player = null;

    private final Map<Ladder, Kit> kits = new HashMap<>();
    @Getter private KitHandler kitHandler;

    @Getter @Setter private PlayerState state = PlayerState.AT_SPAWN;

    @MongoColumn(name = "elo")
    @DatabaseSerializer(serializer = LadderIntegerSerializer.class)
    private Map<Ladder, Integer> elo = new HashMap<>();

    @MongoColumn(name = "kills")
    @DatabaseSerializer(serializer = LadderIntegerSerializer.class)
    @Getter private Map<Ladder, Integer> kills = new HashMap<>();

    @MongoColumn(name = "deaths")
    @DatabaseSerializer(serializer = LadderIntegerSerializer.class)
    @Getter private Map<Ladder, Integer> deaths = new HashMap<>();

    @MongoColumn(name = "wins")
    @DatabaseSerializer(serializer = LadderIntegerSerializer.class)
    @Getter private Map<Ladder, Integer> wins = new HashMap<>();

    @MongoColumn(name = "losses")
    @DatabaseSerializer(serializer = LadderIntegerSerializer.class)
    @Getter private Map<Ladder, Integer> losses = new HashMap<>();

    @Getter @Setter private boolean staffMode = false;

    @Getter @Setter private long enderpearl = 0;

    @Getter @Setter private long duelRequestCooldown = 0;

    @Getter private List<DuelRequest> duelRequests = new ArrayList<>();

    @Getter @Setter private KitBuilder kitBuilder;

    @Getter private PracticeScoreboard scoreboard;

    public void setup() {
        if (player == null) {
            player = Bukkit.getPlayer(name);
        }
        kitHandler = new KitHandler(this);
        for (Ladder ladder : Ladder.getLadders()) {
            if (kitHandler.hasKitSaved(ladder.getName())) {
                kits.put(ladder, kitHandler.load(ladder.getName()));
            }

            if(!elo.containsKey(ladder)){
                elo.put(ladder, DEFAULT_ELO);
            }
            if(!kills.containsKey(ladder)){
                kills.put(ladder, 0);
            }
            if(!deaths.containsKey(ladder)){
                deaths.put(ladder, 0);
            }
            if(!wins.containsKey(ladder)){
                wins.put(ladder, 0);
            }
            if(!losses.containsKey(ladder)){
                losses.put(ladder, 0);
            }
        }
        this.scoreboard = new PracticeScoreboard(this);
    }

    public void handlePlayerVisibility(){
        if(!staffMode){
            for(Player pl : Bukkit.getOnlinePlayers()){
                IPlayer ipl = Practice.getCache().getIPlayer(pl);

                if(getState() == PlayerState.AT_SPAWN){
                    if(ipl.getState() == PlayerState.BUILDING_KIT){
                        //this player is @ spawn, other player is building kit
                        ipl.getPlayer().hidePlayer(player);
                        player.showPlayer(pl);
                    }
                    else if (ipl.getState() == PlayerState.IN_MATCH){
                        //this player is @ spawn, other player is in match
                        Practice.getEntityHider().hideEntity(player, pl);
                        Practice.getEntityHider().hideEntity(pl, player);
                    }
                    else if (ipl.getState() == PlayerState.AT_SPAWN){
                        //both players are at spawn
                        Practice.getEntityHider().showEntity(player, pl);
                        Practice.getEntityHider().showEntity(pl, player);
                        pl.showPlayer(player);
                        player.showPlayer(pl);
                    }
                }
                else if(getState() == PlayerState.IN_MATCH){
                    //this player is in a match
                    if(ipl.getState() == PlayerState.IN_MATCH){
                        //if both players are in match and are in the same match, we dont want to hide them from eachother
                        if(Practice.getMatchManager().getMatch(ipl).getId().equals(Practice.getMatchManager().getMatch(this).getId())){
                            Practice.getEntityHider().showEntity(player, pl);
                            Practice.getEntityHider().showEntity(pl, player);
                            pl.showPlayer(player);
                            player.showPlayer(pl);
                        }
                        else{
                            Practice.getEntityHider().hideEntity(player, pl);
                            Practice.getEntityHider().hideEntity(pl, player);
                        }
                    }
                    else{
                        Practice.getEntityHider().hideEntity(player, pl);
                        Practice.getEntityHider().hideEntity(pl, player);
                    }
                }
                else if (getState() == PlayerState.BUILDING_KIT){
                    player.hidePlayer(pl);
                }
            }
        }
        else{
            for(Player pl : Bukkit.getOnlinePlayers()){
                Practice.getEntityHider().showEntity(player, pl);
                player.showPlayer(pl);
                Practice.getEntityHider().hideEntity(pl, player);
            }
        }
    }

    public Party getParty(){
        return Practice.getPartyManager().getParty(player);
    }

    public void sendToSpawnNoTp() {
        this.state = PlayerState.AT_SPAWN;
        player.setGameMode(GameMode.SURVIVAL);
        //player.teleport(Practice.getIConfig().getSpawn());
        player.setHealth(20);
        player.setFoodLevel(20);
        for(PotionEffect po : player.getActivePotionEffects())
            player.removePotionEffect(po.getType());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        Practice.getSpawn().giveItems(this);
        player.updateInventory();
        scoreboard.update();
    }

    public void sendToSpawn() {
        this.state = PlayerState.AT_SPAWN;
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(Practice.getIConfig().getSpawn());
        player.setHealth(20);
        player.setFoodLevel(20);
        for(PotionEffect po : player.getActivePotionEffects())
            player.removePotionEffect(po.getType());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        Practice.getSpawn().giveItems(this);
        player.updateInventory();
        scoreboard.update();
        handlePlayerVisibility();
    }

    public void setKit(Ladder ladder, Kit kit){
        kits.put(ladder, kit);
    }

    public Kit getKit(Ladder ladder){
        if(!kits.containsKey(ladder)){
            return ladder.getDefaultKit();
        }
        return kits.get(ladder);
    }

    public void equipKit(Ladder ladder){
        if(kits.containsKey(ladder)){
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[0]);

            Kit kit = kits.get(ladder);
            player.getInventory().setArmorContents(kit.getArmor());
            player.getInventory().setContents(kit.getInventory());

            player.getInventory().setContents(player.getInventory().getContents());
            player.updateInventory();
        }
        else {
            Kit kit = ladder.getDefaultKit();
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[0]);

            player.getInventory().setArmorContents(kit.getArmor());
            player.getInventory().setContents(kit.getInventory());

            player.getInventory().setContents(player.getInventory().getContents());
            player.updateInventory();
        }
        player.setGameMode(GameMode.SURVIVAL);
    }

    public int getElo(Ladder ladder){
        return elo.get(ladder);
    }

    public void updateElo(Ladder ladder, int loser, boolean didWin){
        int winner = getElo(ladder);
        double score;
        if(didWin){
            score = 1.0;
        }
        else{
            score = 0.0;
        }
        elo.put(ladder, Elo.getInstance().getNewRating(winner,loser,score));
    }

    public int getAverageElo(){
        int i = 0;
        int count = 0;
        for(Ladder l : elo.keySet()){
            i += elo.get(l);
            count++;
        }
        return Math.round(i / count);
    }

    public double getKDR(Ladder ladder){
        int kills = this.kills.get(ladder);
        int deaths = this.deaths.get(ladder);
        if(kills == 0 && deaths == 0) return 0;
        if(deaths == 0) return kills;
        if(kills == 0) return (double) 1 / (double)deaths;
        return (double)kills / (double)deaths;
    }

    public int getTotalMatches(Ladder ladder){
        if(!wins.containsKey(ladder)){
            wins.put(ladder, 0);
        }
        if(!losses.containsKey(ladder)){
            losses.put(ladder, 0);
        }
        return wins.get(ladder) + losses.get(ladder);
    }

    public int getTotalMatchesAllLadders(){
        int i = 0;

        for(Ladder ladder : Ladder.getLadders()){
            i += getTotalMatches(ladder);
        }

        return i;
    }

    public int getKillsAllLadders(){
        int i = 0;

        for(Ladder ladder : kills.keySet()){
            i += getKills().get(ladder);
        }

        return i;
    }

    public int getDeathsAllLadders(){
        int i = 0;

        for(Ladder ladder : deaths.keySet()){
            i += getDeaths().get(ladder);
        }

        return i;
    }

}
