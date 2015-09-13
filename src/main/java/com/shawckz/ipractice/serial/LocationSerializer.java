package com.shawckz.ipractice.serial;

import com.shawckz.ipractice.configuration.AbstractSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer extends AbstractSerializer<Location> {

    @Override
    public String toString(Location data) {
        String s = data.getWorld().getName()+",";
        s += data.getBlockX()+",";
        s += data.getBlockY()+",";
        s += data.getBlockZ()+",";
        s += data.getPitch()+",";
        s += data.getYaw();
        return s;
    }

    @Override
    public Location fromString(Object data) {
        if(data instanceof String){
            String[] split = ((String)data).split(",");

            World world = Bukkit.getWorld(split[0]);
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            int z = Integer.parseInt(split[3]);
            float pitch = Float.parseFloat(split[4]);
            float yaw = Float.parseFloat(split[5]);

            return new Location(world, x, y, z, yaw, pitch);
        }
        else{
            throw new RuntimeException("LocationSerializer Object data is not a String?");
        }
    }
}
