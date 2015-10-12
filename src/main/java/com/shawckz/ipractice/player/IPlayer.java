package com.shawckz.ipractice.player;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.database.mongo.annotations.CollectionName;
import com.shawckz.ipractice.database.mongo.annotations.DatabaseSerializer;
import com.shawckz.ipractice.database.mongo.annotations.MongoColumn;
import com.shawckz.ipractice.kit.Kit;
import com.shawckz.ipractice.kit.KitBuilder;
import com.shawckz.ipractice.kit.KitHandler;
import com.shawckz.ipractice.kite.KiteRequest;
import com.shawckz.ipractice.match.DuelRequest;
import com.shawckz.ipractice.ladder.Ladder;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.match.PracticeMatch;
import com.shawckz.ipractice.party.Party;
import com.shawckz.ipractice.player.cache.CachePlayer;
import com.shawckz.ipractice.rating.Elo;
import com.shawckz.ipractice.scoreboard.practice.PracticeScoreboard;
import com.shawckz.ipractice.serial.LadderIntegerSerializer;
import com.shawckz.ipractice.util.nametag.NametagManager;
import com.shawckz.ipractice.util.nametag.NametagPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@RequiredArgsConstructor
@CollectionName(name = "iplayers")
@Getter
@Setter
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

    private boolean staffMode = false;
    private long enderpearl = 0;
    private long duelRequestCooldown = 0;
    private List<DuelRequest> duelRequests = new ArrayList<>();
    private List<KiteRequest> kiteRequests = new ArrayList<>();
    private KitBuilder kitBuilder;
    private PracticeScoreboard scoreboard;
    private boolean staffTeleportShuffle = false;
    private Set<String> watching = new HashSet<>();
    private String spectatingMatch = null;

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
        if(!staffMode){}
        else{
            for(Player pl : Bukkit.getOnlinePlayers()){
                handleStaffModeVisibility(pl);
            }
        }
    }

    private void handleStaffModeVisibility(Player pl){
        if(watching.isEmpty()) {
            IPlayer tpl = Practice.getCache().getIPlayer(pl);
            player.showPlayer(pl);
            if (!tpl.isStaffMode()) {
                //Allow staff members to see each other
                pl.hidePlayer(player);
            }
        }
        else{
            IPlayer tpl = Practice.getCache().getIPlayer(pl);
            if(!tpl.isStaffMode()){
                if(watching.contains(tpl.getName())){
                    player.showPlayer(pl);
                }
                else {
                    player.hidePlayer(pl);
                }
                pl.hidePlayer(player);
            }
            else{
                pl.showPlayer(player);
                player.showPlayer(pl);
            }
        }
    }

    public Party getParty(){
        return Practice.getPartyManager().getParty(player);
    }

    public void incrementWins(Ladder ladder){
        if(!wins.containsKey(ladder)){
            wins.put(ladder, 0);
        }
        wins.put(ladder, (wins.get(ladder)+1));
    }

    public void incrementLosses(Ladder ladder){
        if(!losses.containsKey(ladder)){
            losses.put(ladder, 0);
        }
        losses.put(ladder, (losses.get(ladder)+1));
    }

    public void sendToSpawnNoTp() {
        this.state = PlayerState.AT_SPAWN;
        this.spectatingMatch = null;
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        for(PotionEffect po : player.getActivePotionEffects()) {
            player.removePotionEffect(po.getType());
        }
        player.getActivePotionEffects().clear();

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        Practice.getSpawn().giveItems(this);
        player.updateInventory();
        player.getInventory().setContents(player.getInventory().getContents());
        scoreboard.update();
        handlePlayerVisibility();
        NametagManager.getPlayer(player).reset();
        player.spigot().setCollidesWithEntities(true);
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.canSee(player)){
                NametagManager.getPlayer(pl).setPlayerNametag(NametagManager.getPlayer(player), Match.NORMAL);
            }
            if(player.canSee(pl)){
                NametagManager.getPlayer(player).setPlayerNametag(NametagManager.getPlayer(pl), Match.NORMAL);
            }
        }
    }

    public void sendToSpawn() {
        sendToSpawnNoTp();
        player.teleport(Practice.getIConfig().getSpawn());
    }

    public void makeSpectator(PracticeMatch match){
        this.state = PlayerState.SPECTATING_MATCH;
        this.spectatingMatch = match.getId();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        for(PotionEffect po : player.getActivePotionEffects()) {
            player.removePotionEffect(po.getType());
        }
        player.getActivePotionEffects().clear();

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        Practice.getSpawn().giveItems(this);
        player.updateInventory();
        player.spigot().setCollidesWithEntities(false);
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
        if(kits.containsKey(ladder) && ladder.isEditable()){
            Kit kit = kits.get(ladder);
            player.setHealth(20);
            player.setFoodLevel(20);
            for(PotionEffect po : player.getActivePotionEffects())
                player.removePotionEffect(po.getType());

            player.getInventory().setHelmet(kit.getArmor()[3]);
            player.getInventory().setChestplate(kit.getArmor()[2]);
            player.getInventory().setLeggings(kit.getArmor()[1]);
            player.getInventory().setBoots(kit.getArmor()[0]);

            player.getInventory().setContents(kit.getInventory());
        }
        else {
            Kit kit = ladder.getDefaultKit();
            player.setHealth(20);
            player.setFoodLevel(20);
            for(PotionEffect po : player.getActivePotionEffects())
                player.removePotionEffect(po.getType());

            player.getInventory().setHelmet(kit.getArmor()[3]);
            player.getInventory().setChestplate(kit.getArmor()[2]);
            player.getInventory().setLeggings(kit.getArmor()[1]);
            player.getInventory().setBoots(kit.getArmor()[0]);

            player.getInventory().setContents(kit.getInventory());
        }
        player.setGameMode(GameMode.SURVIVAL);
    }

    public int getElo(Ladder ladder){
        if(!elo.containsKey(ladder)){
            elo.put(ladder, DEFAULT_ELO);
        }
        return elo.get(ladder);
    }

    public void setElo(Ladder ladder, int elo){
        this.elo.put(ladder, elo);
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
        for(Ladder l : Ladder.getLadders()){
            if(!elo.containsKey(l)){
                elo.put(l, DEFAULT_ELO);
            }
        }
        for(Ladder l : elo.keySet()){
            i += elo.get(l);
            count++;
        }
        if(i == 0){
            i = 1;
        }
        if(count == 0){
            count = 1;
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
        return (wins.get(ladder) + losses.get(ladder));
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
