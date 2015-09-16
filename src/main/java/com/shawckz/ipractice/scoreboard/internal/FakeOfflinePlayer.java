package com.shawckz.ipractice.scoreboard.internal;

import com.google.common.base.Charsets;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Created by 360 on 9/15/2015.
 */
public class FakeOfflinePlayer implements OfflinePlayer {

    private final String name;
    private final UUID uuid;

    public FakeOfflinePlayer(String name) {
        this.name = name;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public void setBanned(boolean b) {

    }

    @Override
    public boolean isWhitelisted() {
        return false;
    }

    @Override
    public void setWhitelisted(boolean b) {

    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public long getFirstPlayed() {
        return 0;
    }

    @Override
    public long getLastPlayed() {
        return 0;
    }

    @Override
    public boolean hasPlayedBefore() {
        return true;
    }

    @Override
    public Location getBedSpawnLocation() {
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean b) {

    }
}
