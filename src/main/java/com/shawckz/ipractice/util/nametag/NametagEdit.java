package com.shawckz.ipractice.util.nametag;

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by 360 on 2/25/2015.
 */
public class NametagEdit {

    public static enum NametagParam {
        CREATE(0),
        REMOVE(1),
        UPDATE(2),
        ADD_PLAYER(3),
        REMOVE_PLAYER(4);
        private int param;
        NametagParam(int param){
            this.param = param;
        }

        public int getParam() {
            return param;
        }
    }

    private static Method getHandle;
    private static Method sendPacket;
    private static Field playerConnection;
    private static Class<?> packetType;
    private PacketPlayOutScoreboardTeam packet;

    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = false;

    public NametagEdit(){
        this.packet = new PacketPlayOutScoreboardTeam();
        setField("g",packOptionData());
    }

    public void setNametag(Nametag nametag){
        setDisplayName(nametag.getName());
        setName(nametag.getName());
        setPrefix(nametag.getPrefix());
        setSuffix(nametag.getSuffix());
        setCanSeeFriendlyInvisibles(nametag.isCanSeeFriendlyInvisibles());
        setAllowFriendlyFire(nametag.isAllowFriendlyFire());
    }

    public boolean isCanSeeFriendlyInvisibles() {
        return canSeeFriendlyInvisibles;
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        setField("g",packOptionData());
    }

    public boolean isAllowFriendlyFire() {
        return allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        this.allowFriendlyFire = allowFriendlyFire;
        setField("g",packOptionData());
    }

    public int packOptionData() {
        int var1 = 0;
        if(this.allowFriendlyFire) {
            var1 |= 1;
        }

        if(this.canSeeFriendlyInvisibles) {
            var1 |= 2;
        }

        return var1;
    }

    public void setParam(NametagParam param){
        this.setField("f",param.getParam());
    }

    public void setName(String name){
        this.setField("a", name);
    }

    public void setDisplayName(String displayName){
        this.setField("b", displayName);
    }

    public void setPrefix(String prefix){
        this.setField("c", prefix);
    }

    public void setSuffix(String suffix){
        this.setField("d", suffix);
    }

    public void addPlayer(Player p){
        List<String> pp = new ArrayList<>();
        pp.add(p.getName());
        addAll(pp);
    }

    public void removePlayer(Player p){
        remove(p);
    }

    public void sendToPlayer(final Player bukkitPlayer) {
        try {
            ((CraftPlayer)bukkitPlayer).getHandle().playerConnection.sendPacket(packet);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setField(final String field, final Object value) {
        try {
            final Field f = this.packet.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(this.packet, value);
            f.setAccessible(false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addAll(final Collection<?> col) {
        try{
            final Field f = this.packet.getClass().getDeclaredField("e");
            f.setAccessible(true);
            ((Collection)f.get(this.packet)).addAll(col);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void remove(final Player p) {
        try{
            final Field f = this.packet.getClass().getDeclaredField("e");
            f.setAccessible(true);
            if(((Collection)f.get(this.packet)).contains(p.getName())) {
                ((Collection) f.get(this.packet)).remove(p.getName());
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    static {
        try {
            NametagEdit.packetType = Class.forName(ReflectionUtils.getPacketTeamClasspath());
            final Class<?> typeCraftPlayer = Class.forName(ReflectionUtils.getCraftPlayerClasspath());
            final Class<?> typeNMSPlayer = Class.forName(ReflectionUtils.getNMSPlayerClasspath());
            final Class<?> typePlayerConnection = Class.forName(ReflectionUtils.getPlayerConnectionClasspath());
            NametagEdit.getHandle = typeCraftPlayer.getMethod("getHandle", (Class<?>[])new Class[0]);
            NametagEdit.playerConnection = typeNMSPlayer.getField("playerConnection");
            NametagEdit.sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName(ReflectionUtils.getPacketClasspath()));
        }
        catch (Exception e) {
            System.out.println("Failed to setup reflection for Packet209Mod!");
            e.printStackTrace();
        }
    }

}